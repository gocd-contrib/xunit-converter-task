package com.tw.go.plugin.task.xunit;

import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.response.validation.ValidationError;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import com.thoughtworks.go.plugin.api.task.Task;
import com.thoughtworks.go.plugin.api.task.TaskConfig;
import com.thoughtworks.go.plugin.api.task.TaskExecutor;
import com.thoughtworks.go.plugin.api.task.TaskView;
import com.tw.xunit.converter.TestReportConverter;
import org.apache.commons.io.IOUtils;

@Extension
public class XUnitConverterTask implements Task {
    public static final String CONVERTER_TYPE = "ConverterType";
    public static final String INPUT_DIRECTORY = "InputDirectory";
    public static final String OUTPUT_DIRECTORY = "OutputDirectory";

    @Override
    public TaskConfig config() {
        TaskConfig config = new TaskConfig();
        config.addProperty(CONVERTER_TYPE);
        config.addProperty(INPUT_DIRECTORY);
        config.addProperty(OUTPUT_DIRECTORY);
        return config;
    }

    @Override
    public TaskExecutor executor() {
        return new XUnitConverterTaskExecutor();
    }

    @Override
    public TaskView view() {
        TaskView taskView = new TaskView() {
            @Override
            public String displayValue() {
                return "XUnit Converter";
            }

            @Override
            public String template() {
                try {
                    return IOUtils.toString(getClass().getResourceAsStream("/views/task.template.html"), "UTF-8");
                } catch (Exception e) {
                    return "Failed to find template: " + e.getMessage();
                }
            }
        };
        return taskView;
    }

    @Override
    public ValidationResult validate(TaskConfig configuration) {
        ValidationResult validationResult = new ValidationResult();
        if (configuration.getValue(CONVERTER_TYPE) == null) {
            validationResult.addError(new ValidationError(CONVERTER_TYPE, "Converter Type cannot be empty"));
        }
        if (configuration.getValue(INPUT_DIRECTORY) == null) {
            validationResult.addError(new ValidationError(INPUT_DIRECTORY, "Input Directory cannot be empty"));
        }
        if (configuration.getValue(OUTPUT_DIRECTORY) == null) {
            validationResult.addError(new ValidationError(OUTPUT_DIRECTORY, "Output Directory cannot be empty"));
        }

        if (!TestReportConverter.supportsConverter(configuration.getValue(CONVERTER_TYPE))) {
            validationResult.addError(new ValidationError(CONVERTER_TYPE, "Unsupported Converter Type"));
        }

        return validationResult;
    }
}
