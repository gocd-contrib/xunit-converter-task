package com.tw.go.plugin.task.xunit;

import com.thoughtworks.go.plugin.api.response.execution.ExecutionResult;
import com.thoughtworks.go.plugin.api.task.*;
import com.tw.xunit.converter.TestReportConverter;

import java.io.File;
import java.io.IOException;

public class XUnitConverterTaskExecutor implements TaskExecutor {
    @Override
    public ExecutionResult execute(TaskConfig config, TaskExecutionContext taskEnvironment) {
        try {
            return convertToXUnit(taskEnvironment, config);
        } catch (Exception e) {
            return ExecutionResult.failure("Failed to download file from URL: " + config.getValue(XUnitConverterTask.CONVERTER_TYPE), e);
        }
    }

    private ExecutionResult convertToXUnit(TaskExecutionContext taskContext, TaskConfig taskConfig) throws IOException, InterruptedException {
        Console console = taskContext.console();
        console.printLine("Starting conversion...");

        String converterType = taskConfig.getValue(XUnitConverterTask.CONVERTER_TYPE);
        String inputDirectory = taskConfig.getValue(XUnitConverterTask.INPUT_DIRECTORY);
        String outputDirectory = taskConfig.getValue(XUnitConverterTask.OUTPUT_DIRECTORY);

        try {
            new TestReportConverter().convert(converterType, new File(taskContext.workingDir() + "/" + inputDirectory), new File(taskContext.workingDir() + "/" + outputDirectory));
            console.printLine("Conversion finished");
            return ExecutionResult.success("done.");
        } catch (Exception e) {
            console.printLine("Failed to convert. Message: " + e.getMessage());
            return ExecutionResult.failure("Failed to convert. Message: " + e.getMessage());
        }
    }
}