package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import bgu.spl.mics.Input;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.ConferenceService;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.StudentService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

/**
 * This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    static Student[] students;
    static ConfrenceInformation[] conferences;
    static Cluster cluster = Cluster.getInstance();
    static LinkedList<MicroService> services = new LinkedList<>();
    static LinkedList<Thread> threads = new LinkedList<>();

    public static void main(String[] args) {
        // region PARSING INPUT
        Gson gson = new Gson();
        Input input = null;
        try (Reader reader = new FileReader(args[0])) {
            input = gson.fromJson(reader, Input.class);

        } catch (IOException e) {
            System.out.println("caught exception");
        }

        assert input != null;
        students = input.getStudents();
        for (Student s : students) {
            for (Model m : s.getModels()) m.init(s);
            s.init();
            StudentService studentService = new StudentService(s.getName(), s);
            Thread studentT = new Thread(studentService);
            services.add(studentService);
            threads.add(studentT);
        }

        for (String type : input.getGpus()) {
            GPU gpu = new GPU(type);
            cluster.registerGPU(gpu);
            GPUService gpuService = new GPUService(gpu.getName(), gpu);
            gpu.setMyService(gpuService);
            Thread gpuT = new Thread(gpuService);
            services.add(gpuService);
            threads.add(gpuT);
        }

        int cpuCapacities = 0;
        for (int cores : input.getCpus()) {
            CPU cpu = new CPU(cores);
            cpuCapacities += cpu.getCapacity();
            cluster.registerCPU(cpu);
            CPUService cpuService = new CPUService(cpu.getName(), cpu);
            Thread cpuT = new Thread(cpuService);
            services.add(cpuService);
            threads.add(cpuT);
        }

        cluster.init(cpuCapacities);

        conferences = input.getConferences();
        for (ConfrenceInformation cInfo : conferences) {
            cInfo.init();
            ConferenceService confService = new ConferenceService(cInfo.getName(), cInfo);
            Thread confT = new Thread(confService);
            services.add(confService);
            threads.add(confT);
        }

        // start time service
        TimeService ts = new TimeService(input.getTickTime(), input.getDuration(), threads.size());
        Thread timeT = new Thread(ts);
        services.add(ts);
        timeT.start();

        while (!ts.isReady()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
            ;
        }

        for (Thread t : threads) t.start();
        threads.add(timeT);

        // endregion

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException ignored) {
            }
        }

        Output();
    }

    private static void Output() {
        JsonObject Output = new JsonObject();
        JsonArray studentsOutput = new JsonArray();
        Output.add("students", studentsOutput);
        JsonArray conferences = new JsonArray();
        Output.add("conferences", conferences);
        Output.addProperty("cpuTimeUsed", cluster.cpuTicksUsed());
        Output.addProperty("gpuTimeUsed", cluster.gpuTicksUsed());
        Output.addProperty("batchesProcessed", cluster.cpuTotalProcessedBatches());
        //build Students
        for (int i = 0; i < CRMSRunner.students.length; i++) {
            //build student
            JsonObject student = new JsonObject();
            //insert student data
            student.addProperty("name", CRMSRunner.students[i].getName());
            student.addProperty("department", CRMSRunner.students[i].getDepartment());
            student.addProperty("status", CRMSRunner.students[i].getStatus());
            student.addProperty("publications", CRMSRunner.students[i].getNumOfPublications());
            student.addProperty("papersRead", CRMSRunner.students[i].getNumOfPapers());
            //models
            JsonArray trainedModels = new JsonArray();
            Model[] studentModels = CRMSRunner.students[i].getModels();
            for (int j = 0; j < studentModels.length; j++) {
                if (studentModels[j].isTrained()) {
                    //create model
                    JsonObject model = new JsonObject();
                    model.addProperty("name", studentModels[j].getName());
                    //create model data
                    JsonObject data = new JsonObject();
                    Data ModelData = studentModels[j].getData();
                    data.addProperty("type", ModelData.getTypeS());
                    data.addProperty("size", ModelData.getSize());
                    model.add("data", data);
                    //continue model
                    model.addProperty("status", studentModels[j].getTested());
                    model.addProperty("results", studentModels[j].getResults());
                    trainedModels.add(model);
                }
            }
            student.add("trainedModels", trainedModels);
            //insert student x to students
            studentsOutput.add(student);
        }
        //build conferences
        for (int i = 0; i < CRMSRunner.conferences.length; i++) {
            JsonObject conference = new JsonObject();
            conference.addProperty("name", CRMSRunner.conferences[i].getName());
            conference.addProperty("Date", CRMSRunner.conferences[i].getDate());
            //conference publications
            JsonArray publications = new JsonArray();
            LinkedList<Model> conferencePublications = CRMSRunner.conferences[i].getModels();
            for (int j = 0; conferencePublications != null && j < conferencePublications.size(); j++) {
                //create model
                JsonObject CModel = new JsonObject();
                CModel.addProperty("name", conferencePublications.get(j).getName());
                //create CModel data
                JsonObject CData = new JsonObject();
                Data CModelData = conferencePublications.get(j).getData();
                CData.addProperty("type", CModelData.getTypeS());
                CData.addProperty("size", CModelData.getSize());
                CModel.add("data", CData);
                //continue model
                CModel.addProperty("status", conferencePublications.get(j).getTested());
                CModel.addProperty("results", conferencePublications.get(j).getResults());
                publications.add(CModel);
            }
            conference.add("publications", publications);
            conferences.add(conference);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter file = new FileWriter("Output.json")) {
            gson.toJson(Output, file);
        } catch (IOException e) {
        }
    }
}