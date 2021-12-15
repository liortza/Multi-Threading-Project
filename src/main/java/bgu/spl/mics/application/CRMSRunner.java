package bgu.spl.mics.application;

import bgu.spl.mics.Input;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.ConferenceService;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.StudentService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    static Student[] students;
    static ConfrenceInformation[] conferences;

    public static void main(String[] args) {
        // region PARSING INPUT
        Gson gson = new Gson();
        try (Reader reader = Files.newBufferedReader(Paths.get("example_input.json"))) {
            Input input = gson.fromJson(reader, Input.class);
            students = input.getStudents();
            for (Student s : students) {
                for (Model m : s.getModels()) m.init(s);
                s.init();
                StudentService studentService = new StudentService(s.getName(), s);
                Thread studentT = new Thread(studentService);
                studentT.start();
            }

            for (GPU gpu: input.getGpus()) {
                GPUService gpuService = new GPUService(gpu.getName(), gpu);
                Thread gpuT = new Thread(gpuService);
                gpuT.start();
            }

            for (CPU cpu: input.getCpus()) {
                CPUService cpuService = new CPUService(cpu.getName(), cpu);
                Thread cpuT = new Thread(cpuService);
                cpuT.start();
            }

            conferences = input.getConferences();
            for (ConfrenceInformation cInfo: conferences) {
                ConferenceService confService = new ConferenceService(cInfo.getName(), cInfo);
                Thread confT = new Thread(confService);
                confT.start();
            }

        } catch (IOException e) {
            System.out.println("caught exception");
        }
        // endregion

    }
}
