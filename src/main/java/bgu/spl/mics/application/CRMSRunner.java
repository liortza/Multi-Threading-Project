package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;

import java.io.FileWriter;
import java.io.IOException;

import bgu.spl.mics.Input;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.ConferenceService;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.StudentService;
import com.google.gson.Gson;
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
    static Cluster cluster = new Cluster();
    static LinkedList<MicroService> services = new LinkedList<>();
    static LinkedList<Thread> threads = new LinkedList<>();

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
                services.add(studentService);
                threads.add(studentT);
                studentT.start();
            }

            Cluster cluster = Cluster.getInstance();

            for (String type : input.getGpus()) {
                GPU gpu = new GPU(type);
                cluster.registerGPU(gpu);
                GPUService gpuService = new GPUService(gpu.getName(), gpu);
                Thread gpuT = new Thread(gpuService);
                services.add(gpuService);
                threads.add(gpuT);
                gpuT.start();
            }

            for (int cores : input.getCpus()) {
                CPU cpu = new CPU(cores);
                cluster.registerCPU(cpu);
                CPUService cpuService = new CPUService(cpu.getName(), cpu);
                Thread cpuT = new Thread(cpuService);
                services.add(cpuService);
                threads.add(cpuT);
                cpuT.start();
            }

            conferences = input.getConferences();
            for (ConfrenceInformation cInfo : conferences) {
                ConferenceService confService = new ConferenceService(cInfo.getName(), cInfo);
                Thread confT = new Thread(confService);
                services.add(confService);
                threads.add(confT);
                confT.start();
            }

            // wait for all threads to finish registering and subscribing before starting ticks
            for (MicroService ms : services) {
//                try {
//
//                }
            }

            TimeService ts = new TimeService(input.getTickTime(), input.getDuration());
            Thread timeT = new Thread(ts);
            services.add(ts);
            threads.add(timeT);
            timeT.start();

        } catch (IOException e) {
            System.out.println("caught exception");
        }
        // endregion

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {}
        }

        Output();

    }

    private static void Output() {
        JsonArray Output = new JsonArray();
        JsonArray studentsOutput = new JsonArray();
        //Output.set(0, studentsOutput);
        Output.add(studentsOutput);
        JsonArray conferences = new JsonArray();
        Output.add(conferences);
        JsonObject cpuTimeUsed = new JsonObject();
        cpuTimeUsed.addProperty("cpuTimeUsed", cluster.cpuTicksUsed());
        Output.add(cpuTimeUsed);
        JsonObject gpuTimeUsed = new JsonObject();
        gpuTimeUsed.addProperty("gpuTimeUsed", cluster.gpuTicksUsed());
        Output.add(gpuTimeUsed);
        JsonObject batchesProcessed = new JsonObject();
        batchesProcessed.addProperty("batchesProcessed", cluster.cpuTotalProcessedBatches());
        Output.add(batchesProcessed);
        //build Students
        for (int i = 0; i < CRMSRunner.students.length; i++) {
            //build student
            JsonArray student = new JsonArray();
            //insert student data
            JsonObject name = new JsonObject();
            name.addProperty("name", CRMSRunner.students[i].getName());
            student.add(name);
            JsonObject department = new JsonObject();
            department.addProperty("department", CRMSRunner.students[i].getDepartment());
            student.add(department);
            JsonObject status = new JsonObject();
            status.addProperty("status", CRMSRunner.students[i].getStatus());
            student.add(status);
            JsonObject publications = new JsonObject();
            publications.addProperty("publications", CRMSRunner.students[i].getNumOfPublications());
            student.add(publications);
            JsonObject papersRead = new JsonObject();
            papersRead.addProperty("papersRead", CRMSRunner.students[i].getNumOfPapers());
            student.add(papersRead);
            //models
            JsonArray trainedModels = new JsonArray();
            Model[] studentModels = CRMSRunner.students[i].getModels();
            for (int j = 0; j < studentModels.length; j++) {
                if (studentModels[j].isTrained()) {
                    //create model
                    JsonArray model = new JsonArray();
                    JsonObject modelName = new JsonObject();
                    modelName.addProperty("name", studentModels[j].getName());
                    model.add(modelName);
                    //create model data
                    JsonArray data = new JsonArray();
                    Data ModelData = studentModels[j].getData();
                    JsonObject type = new JsonObject();
                    type.addProperty("type", ModelData.getTypeS());
                    data.add(type);
                    JsonObject size = new JsonObject();
                    size.addProperty("size", ModelData.getSize());
                    data.add(size);
                    model.add(data);
                    //continue model
                    JsonObject modelStatus = new JsonObject();
                    modelStatus.addProperty("status", studentModels[j].getTested());
                    model.add(modelName);
                    JsonObject results = new JsonObject();
                    results.addProperty("results", studentModels[j].getResults());
                    model.add(results);
                    trainedModels.add(model);
                }
            }
            student.add(trainedModels);
            //insert student x to students
            studentsOutput.add(student);
        }
        //build conferences
        for (int i = 0; i < CRMSRunner.conferences.length; i++) {
            JsonObject conferenceName = new JsonObject();
            conferenceName.addProperty("name", CRMSRunner.conferences[i].getName());
            conferences.add(conferenceName);
            JsonObject conferenceDate = new JsonObject();
            conferenceDate.addProperty("Date", CRMSRunner.conferences[i].getDate());
            conferences.add(conferenceDate);
            //conference publications
            JsonArray publications = new JsonArray();
            LinkedList<Model> conferencePublications = CRMSRunner.conferences[i].getModels();
            for (int j = 0;conferencePublications!=null && j < conferencePublications.size(); j++) {
                //create model
                JsonArray CModel = new JsonArray();
                JsonObject CModelName = new JsonObject();
                CModelName.addProperty("name", conferencePublications.get(j).getName());
                CModel.add(CModelName);
                //create CModel data
                JsonArray CData = new JsonArray();
                Data CModelData = conferencePublications.get(j).getData();
                JsonObject CType = new JsonObject();
                CType.addProperty("type", CModelData.getTypeS());
                CData.add(CType);
                JsonObject CSize = new JsonObject();
                CSize.addProperty("size", CModelData.getSize());
                CData.add(CSize);
                CModel.add(CData);
                //continue model
                JsonObject CModelStatus = new JsonObject();
                CModelStatus.addProperty("status", conferencePublications.get(j).getTested());
                CModel.add(CModelStatus);
                JsonObject CResults = new JsonObject();
                CResults.addProperty("results", conferencePublications.get(j).getResults());
                CModel.add(CResults);
                publications.add(CModel);
            }
            conferences.add(publications);
        }
        try {
            FileWriter file = new FileWriter("E:/output.json");
            file.write(Output.toString());
            file.close();
        } catch (IOException e) {
        }
        System.out.println("JSON file created: " + Output);
    }
}

