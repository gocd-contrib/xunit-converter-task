package com.tw.go.plugin.task.xunit;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.execution.ExecutionResult;
import com.thoughtworks.go.plugin.api.task.*;
import com.tw.xunit.converter.TestReportConverter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class XUnitConverterTaskExecutor {
    private final String converterType;
    private final String inputDirectory;
    private final String outputDirectory;
    private GsonBuilder gsonBuilder;

    public XUnitConverterTaskExecutor(GsonBuilder gsonBuilder, String converterType, String inputDirectory, String outputDirectory) {
        this.gsonBuilder = gsonBuilder;
        this.converterType = converterType;
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
    }

    public GoPluginApiResponse execute(String workingDir) {
        try {
            return convertToXUnit(workingDir);
        } catch (Exception e) {
            return DefaultGoPluginApiResponse.success(message(false, "Failed to download file from URL for: " + converterType + ". Error: " + e.getMessage()));
        }
    }

    private GoPluginApiResponse convertToXUnit(String workingDir) throws IOException, InterruptedException {
        JobConsoleLogger console = JobConsoleLogger.getConsoleLogger();
        console.printLine("Starting conversion...");
        
        try {
            new TestReportConverter().convert(converterType, new File(workingDir + "/" + inputDirectory), new File(workingDir + "/" + outputDirectory));
            console.printLine("Conversion finished");
            return DefaultGoPluginApiResponse.success(message(true, "Done"));
        } catch (Exception e) {
            console.printLine("Failed to convert. Message: " + e.getMessage());
            return DefaultGoPluginApiResponse.success(message(false, "Failed to convert. Message: " + e.getMessage()));
        }
    }

    private String message(boolean status, String message) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("success", status);
        result.put("message", message);
        return gsonBuilder.create().toJson(result);
    }
}