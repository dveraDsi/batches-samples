package fr.dsidiff.dsipilot.csvparser;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.Date;

//@EnableBatchProcessing
@SpringBootApplication
@RequiredArgsConstructor
public class DsipilotCsvParserApplication implements CommandLineRunner {
    private final JobLauncher jobLauncher;
    private final Job csvJob;

    @Value("${output.directory}")
    private String outputDir;

    public static void main(String[] args) {
        SpringApplication.run(DsipilotCsvParserApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        StopWatch stopwatch = StopWatch.createStarted();
        if (args.length == 0) {
            System.err.println("❌ Please provide the path to the CSV file as an argument.");
            return;
        }

        String filePath = args[0];
        File file = new File(filePath);

        if (!file.exists() || !file.getName().toLowerCase().endsWith(".csv")) {
            System.err.println("❌ Invalid CSV file path provided.");
            return;
        }

        // Ensure extension is lowercase (optional)
        String adjustedPath = setExtensionToLowerCase(file);

        JobParameters params = new JobParametersBuilder()
                .addString("filePath", adjustedPath)
                .addString("outputFile", outputDir + File.separator + file.getName().replace(".csv", "_out.txt"))
                .addDate("timestamp", new Date())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(csvJob, params);
        System.out.println("✅ Job Status: " + execution.getStatus());
        stopwatch.stop();
        System.out.println("Total time: " + stopwatch.getDuration().toMillis() + " ms");
    }

    private String setExtensionToLowerCase(File originalFile) {
        String name = originalFile.getName();
        int dotIndex = name.lastIndexOf('.');
        String nameWithoutExtension = name.substring(0, dotIndex);
        String extension = name.substring(dotIndex + 1);

        String newFileName = nameWithoutExtension + "." + extension.toLowerCase();
        File renamedFile = new File(originalFile.getParent(), newFileName);

        if (!originalFile.getName().equals(renamedFile.getName()) && originalFile.renameTo(renamedFile)) {
            System.out.println("File renamed to: " + renamedFile.getPath());
            return renamedFile.getAbsolutePath();
        }

        return originalFile.getAbsolutePath();
    }
}
