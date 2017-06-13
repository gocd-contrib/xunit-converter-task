package com.tw.go.plugin.task.xunit;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.apache.commons.io.IOUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;

@Extension
public class XUnitConverterTask implements GoPlugin {
    public static final String CONVERTER_TYPE = "ConverterType";
    public static final String INPUT_DIRECTORY = "InputDirectory";
    public static final String OUTPUT_DIRECTORY = "OutputDirectory";
    private GsonBuilder gsonBuilder;

    public XUnitConverterTask() {
        this.gsonBuilder = new GsonBuilder();
    }

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return new GoPluginIdentifier("task", asList("1.0"));
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException {
        if ("configuration".equals(request.requestName())) {
            return configuration();
        } else if ("view".equals(request.requestName())) {
            return view();
        } else if ("validate".equals(request.requestName())) {
            return validate(request);
        } else if ("execute".equals(request.requestName())) {
            return execute(request);
        }
        throw new UnhandledRequestTypeException(request.requestName());
    }

    private GoPluginApiResponse configuration() {
        HashMap<String, Object> configSchema = new HashMap<String, Object>();
        configSchema.put(CONVERTER_TYPE, configProperty("", false, false));
        configSchema.put(INPUT_DIRECTORY, configProperty("", false, false));
        configSchema.put(OUTPUT_DIRECTORY, configProperty("", false, false));
        return DefaultGoPluginApiResponse.success(gsonBuilder.create().toJson(configSchema));
    }

    private GoPluginApiResponse view() {
        Map<String, Object> viewResponse = new HashMap<String, Object>();
        viewResponse.put("displayValue", "XUnit Converter");

        try {
            viewResponse.put("template", IOUtils.toString(getClass().getResourceAsStream("/views/task.template.html"), "UTF-8"));
            return DefaultGoPluginApiResponse.success(gsonBuilder.create().toJson(viewResponse));
        } catch (Exception e) {
            return DefaultGoPluginApiResponse.error("Failed to read template: " + e.getMessage());
        }
    }

    private GoPluginApiResponse validate(GoPluginApiRequest request) {
        Map<String, Object> validationResponse = new HashMap<String, Object>();

        Type type = new TypeToken<Map<String, Map<String, Object>>>() {}.getType();
        Map<String, Map<String, Object>> configWithValues = gsonBuilder.create().fromJson(request.requestBody(), type);

        if (hasEmptyValue(configWithValues.get(CONVERTER_TYPE))) {
            validationResponse.put(CONVERTER_TYPE, "Converter Type cannot be empty");
        }
        if (hasEmptyValue(configWithValues.get(INPUT_DIRECTORY))) {
            validationResponse.put(INPUT_DIRECTORY, "Input Directory cannot be empty");
        }
        if (hasEmptyValue(configWithValues.get(OUTPUT_DIRECTORY))) {
            validationResponse.put(OUTPUT_DIRECTORY, "Output Directory cannot be empty");
        }

        return DefaultGoPluginApiResponse.success(gsonBuilder.create().toJson(validationResponse));
    }

    private GoPluginApiResponse execute(GoPluginApiRequest request) {
        Map executionRequest = gsonBuilder.create().fromJson(request.requestBody(), Map.class);
        Map<String, Object> context = (Map<String, Object>) executionRequest.get("context");
        Map<String, Object> config = (Map<String, Object>) executionRequest.get("config");

        XUnitConverterTaskExecutor executor = new XUnitConverterTaskExecutor(gsonBuilder,
                ((Map<String, String>) config.get(CONVERTER_TYPE)).get("value"),
                ((Map<String, String>) config.get(INPUT_DIRECTORY)).get("value"),
                ((Map<String, String>) config.get(OUTPUT_DIRECTORY)).get("value"));

        return executor.execute((String) context.get("workingDirectory"));
    }

    private boolean hasEmptyValue(Map<String, Object> fieldInConfigWithValue) {
        if (fieldInConfigWithValue == null) {
            return true;
        }
        String value = (String) fieldInConfigWithValue.get("value");
        return value == null || value.matches("^[ \t]*$");
    }

    private Map<String, Object> configProperty(String defaultValue, boolean required, boolean secure) {
        HashMap<String, Object> property = new HashMap<String, Object>();
        property.put("default-value", defaultValue);
        property.put("required", required);
        property.put("secure", secure);
        return property;
    }
}
