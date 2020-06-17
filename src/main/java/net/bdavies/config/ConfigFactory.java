package net.bdavies.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Scanner;

@Slf4j
public class ConfigFactory {

    public static Config makeConfig(String json) {
        return new ConfigParser(json).getConfig();
    }

    @SneakyThrows
    public static Config makeConfig(File f) {
        // Create a config folder / make sure it exists
        File configFolder = new File("config");
        if (!configFolder.exists()) {
            if (!configFolder.mkdirs()) {
                String error = "Could not create config folder please check permissions.";
                log.error(error);
                throw new RuntimeException(error);
            }
        }

        if (!f.exists()) {
            if (f.createNewFile()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(ConfigFactory.class
                        .getResourceAsStream("/config.example.json")));

                StringBuilder stringBuilder = new StringBuilder();
                String line;

                Scanner scanner = new Scanner(System.in);


                while ((line = reader.readLine()) != null) {

                    if (line.contains("brokerIp")) {
                        System.out.print("Please enter a broker ip: ");
                        while (!scanner.hasNextLine()) {
                        }
                        String brokerIp = scanner.nextLine();
                        line = "\t\"brokerIp\": \"" + brokerIp + "\",";
                    }

                    stringBuilder.append(line).append("\n");

                }

                BufferedWriter writer = new BufferedWriter(new FileWriter(f));
                writer.write(stringBuilder.toString());
                writer.flush();
                writer.close();

            } else {
                throw new RuntimeException("Could not create config file please check permissions.");
            }
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
            return new ConfigParser(bufferedReader).getConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Something went wrong");
    }

}
