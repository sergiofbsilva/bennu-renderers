package pt.ist.bennu.core.presentationTier.servlets;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.tiles.AttributeDefinition;
import org.apache.struts.tiles.ComponentDefinition;
import org.apache.struts.tiles.TilesUtil;
import org.apache.struts.tiles.TilesUtilStrutsModulesImpl;
import org.apache.struts.tiles.definition.ComponentDefinitionsFactoryWrapper;
import org.apache.struts.tiles.xmlDefinition.DefinitionsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixWebFramework.struts.tiles.FenixDefinitionsFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

public class StrutsLogger {

    private static final GsonBuilder gsonBuilder;
    private static final Gson gson;

    private static final Logger logger = LoggerFactory.getLogger(StrutsLogger.class);

    static {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();
    }

    public StrutsLogger(Set<ModuleConfig> modules, ServletConfig config) {
        logModules(modules, config);
    }

    private void logModules(Set<ModuleConfig> modules, ServletConfig config) {

        for (ModuleConfig moduleConfig : modules) {
            logModule(moduleConfig, config);
        }
    }

    public void logModule(ModuleConfig moduleConfig, ServletConfig config) {
        try {
            String prefix = getModulePrefixWithStartingSlash(moduleConfig);
            String filename = "/tmp/modules" + prefix + ".json";

            logger.info("dumping json module file {} for module with prefix '{}'", filename, prefix);

            final File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file);
            gson.toJson(toJson(moduleConfig, config), fw);
            fw.close();
        } catch (JsonIOException | IOException e) {
            e.printStackTrace();
        }
    }

    private String getModulePrefixWithStartingSlash(ModuleConfig moduleConfig) {
        String prefix = moduleConfig.getPrefix();
        if (StringUtils.isEmpty(prefix)) {
            prefix = "/default";
        } else {
            prefix = StringUtils.startsWith(prefix, "/") ? StringUtils.removeStart(prefix, "/") : prefix;
            prefix = prefix.replace("/", "_");
        }
        prefix = StringUtils.startsWith(prefix, "/") ? prefix : "/" + prefix;
        return prefix;
    }

    private JsonElement toJson(ModuleConfig moduleConfig, ServletConfig config) {
        JsonObject module = new JsonObject();
        module.addProperty("prefix", moduleConfig.getPrefix());

        JsonArray actions = new JsonArray();
        for (ActionConfig actionConfig : moduleConfig.findActionConfigs()) {
            actions.add(toJson(actionConfig));
        }

        module.add("tiles", tilesToJson(moduleConfig, config));

        module.add("actions", actions);

        return module;

    }

    private JsonElement tilesToJson(ModuleConfig moduleConfig, ServletConfig config) {

        TilesUtilStrutsModulesImpl tilesUtil = (TilesUtilStrutsModulesImpl) TilesUtil.getTilesUtil();

        ComponentDefinitionsFactoryWrapper definitionsFactory =
                (ComponentDefinitionsFactoryWrapper) tilesUtil.getDefinitionsFactory(config.getServletContext(), moduleConfig);

        if (definitionsFactory == null) {
            return new JsonObject();
        }

        JsonObject tilesJson = new JsonObject();

        final Object attribute = definitionsFactory.getConfig().getAttribute("defaultTileDefinition");
        tilesJson.addProperty("defaultTileDefinition", attribute == null ? "null" : attribute.toString());

        FenixDefinitionsFactory internalFactory = (FenixDefinitionsFactory) definitionsFactory.getInternalFactory();
        Class<?> internalFactoryClass = internalFactory.getClass().getSuperclass();
        JsonArray definitionsJson = new JsonArray();

        try {
            Method declaredMethod = internalFactoryClass.getDeclaredMethod("getDefaultFactory");
            declaredMethod.setAccessible(true);
            DefinitionsFactory defaultFactory = (DefinitionsFactory) declaredMethod.invoke(internalFactory);
            Class<? extends DefinitionsFactory> defaultFactoryClass = defaultFactory.getClass();
            Field definitions = defaultFactoryClass.getDeclaredField("definitions");
            definitions.setAccessible(true);
            Map<String, ComponentDefinition> defsMap = (Map<String, ComponentDefinition>) definitions.get(defaultFactory);

            for (String key : defsMap.keySet()) {
                final ComponentDefinition comp = defsMap.get(key);
                JsonObject defJson = new JsonObject();
                defJson.addProperty("name", comp.getName());
                defJson.addProperty("page", comp.getPage());
                defJson.addProperty("path", comp.getPath());
                JsonObject attr = new JsonObject();
                for (Object attrKey : comp.getAttributes().keySet()) {
                    final Object object = comp.getAttributes().get(attrKey);
                    if (object == null) {
                        continue;
                    }
                    if (object instanceof AttributeDefinition) {
                        final AttributeDefinition attributeDefinition = (AttributeDefinition) object;
                        attr.addProperty((String) attrKey, (String) attributeDefinition.getValue());
                    } else if (object instanceof String) {
                        attr.addProperty((String) attrKey, (String) object);
                    } else {
                        logger.info("object type > " + object.getClass().getName());
                    }
                }
                defJson.add("attributes", attr);
                definitionsJson.add(defJson);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        tilesJson.add("definitions", definitionsJson);
        return tilesJson;
    }

    private JsonElement toJson(ActionConfig actionConfig) {
        final JsonObject json = new JsonObject();
        json.addProperty("name", actionConfig.getName());
        json.addProperty("path", actionConfig.getPath());
        json.addProperty("forward", actionConfig.getForward());
        json.addProperty("type", actionConfig.getType());
        json.add("forwards", toJson(actionConfig.findForwardConfigs()));
        return json;
    }

    private JsonElement toJson(ForwardConfig[] findForwardConfigs) {
        JsonArray forwards = new JsonArray();
        for (ForwardConfig forwardConfig : findForwardConfigs) {
            forwards.add(toJson(forwardConfig));
        }
        return forwards;
    }

    private JsonElement toJson(ForwardConfig forwardConfig) {
        JsonObject forward = new JsonObject();
        forward.addProperty("name", forwardConfig.getName());
        forward.addProperty("path", forwardConfig.getPath());
        forward.addProperty("redirect", forwardConfig.getRedirect());
        forward.addProperty("contextRelative", forwardConfig.getContextRelative());
        return forward;
    }
}
