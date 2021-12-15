package bgu.spl.mics.application;

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

            Cluster cluster = Cluster.getInstance();

            for (String type : input.getGpus()) {
                GPU gpu = new GPU(type);
                cluster.registerGPU(gpu);
                GPUService gpuService = new GPUService(gpu.getName(), gpu);
                Thread gpuT = new Thread(gpuService);
                gpuT.start();
            }

            for (int cores : input.getCpus()) {
                CPU cpu = new CPU(cores);
                cluster.registerCPU(cpu);
                CPUService cpuService = new CPUService(cpu.getName(), cpu);
                Thread cpuT = new Thread(cpuService);
                cpuT.start();
            }

            conferences = input.getConferences();
            for (ConfrenceInformation cInfo : conferences) {
                ConferenceService confService = new ConferenceService(cInfo.getName(), cInfo);
                Thread confT = new Thread(confService);
                confT.start();
            }

            // wait for all threads to finish registering and subscribing before starting ticks

            Thread timeT = new Thread(new TimeService(input.getTickTime(), input.getDuration()));
            timeT.start();

        } catch (IOException e) {
            System.out.println("caught exception");
        }
        // endregion
        Output();

    }

    private static void Output() {
        JsonArray output = new JsonArray();
        JsonArray Output = new JsonArray();
        JsonArray students = new JsonArray();
        Output.set(0, students);
        JsonArray conferences = new JsonArray();
        Output.set(1, conferences);
        JsonObject cpuTimeUsed = new JsonObject();
        cpuTimeUsed.addProperty("cpuTimeUsed", cluster.cpuTicksUsed());
        Output.set(2, cpuTimeUsed);
        JsonObject gpuTimeUsed = new JsonObject();
        gpuTimeUsed.addProperty("gpuTimeUsed", cluster.gpuTicksUsed());
        Output.set(3, gpuTimeUsed);
        JsonObject batchesProcessed = new JsonObject();
        batchesProcessed.addProperty("batchesProcessed", cluster.cpuTotalProcessedBatches());
        Output.set(4, batchesProcessed);
        //build Students
        for (int i = 0; i < CRMSRunner.students.length; i++) {
            //build student
            JsonArray student = new JsonArray();
            //insert student data
            JsonObject name = new JsonObject();
            name.addProperty("name", CRMSRunner.students[i].getName());
            student.set(0, name);
            JsonObject department = new JsonObject();
            department.addProperty("department", CRMSRunner.students[i].getDepartment());
            student.set(1, department);
            JsonObject status = new JsonObject();
            status.addProperty("status", CRMSRunner.students[i].getStatus());
            student.set(2, status);
            JsonObject publications = new JsonObject();
            publications.addProperty("publications", CRMSRunner.students[i].getStatus());
            student.set(3, publications);
            JsonObject papersRead = new JsonObject();
            papersRead.addProperty("papersRead", CRMSRunner.students[i].getNumOfPapers());
            student.set(4, papersRead);
            //models
            JsonArray trainedModels = new JsonArray();
            Model[] studentModels = CRMSRunner.students[i].getModels();
            for (int j = 0; j < studentModels.length; j++) {
                if (studentModels[i].isTrained()) {
                    //create model
                    JsonArray model = new JsonArray();
                    JsonObject modelName = new JsonObject();
                    modelName.addProperty("name", studentModels[i].getName());
                    model.set(0, modelName);
                    //create model data
                    JsonArray data = new JsonArray();
                    Data ModelData = studentModels[i].getData();
                    JsonObject type = new JsonObject();
                    type.addProperty("type", ModelData.getTypeS());
                    data.set(0, type);
                    JsonObject size = new JsonObject();
                    size.addProperty("size", ModelData.getSize());
                    data.set(1, size);
                    model.set(1, data);
                    //continue model
                    JsonObject modelStatus = new JsonObject();
                    modelStatus.addProperty("status", studentModels[i].getTested());
                    model.set(2, modelName);
                    JsonObject results = new JsonObject();
                    results.addProperty("results", studentModels[i].getResults());
                    model.set(3, results);
                    trainedModels.add(model);
                }
            }
            student.set(5, trainedModels);
            //insert student x to students
            students.set(i, student);
        }
        //build conferences
        for (int i = 0; i < CRMSRunner.conferences.length; i++) {
            JsonObject conferenceName = new JsonObject();
            conferenceName.addProperty("name", CRMSRunner.conferences[i].getName());
            conferences.set(0, conferenceName);
            JsonObject conferenceDate = new JsonObject();
            conferenceDate.addProperty("Date", CRMSRunner.conferences[i].getDate());
            conferences.set(1, conferenceDate);
            //conference publications
            JsonArray publications = new JsonArray();
            LinkedList<Model> conferencePublications = CRMSRunner.conferences[i].getModels();
            for (int j = 0; i < conferencePublications.size(); i++) {
                //create model
                JsonArray CModel = new JsonArray();
                JsonObject CModelName = new JsonObject();
                CModelName.addProperty("name", conferencePublications.get(i).getName());
                CModel.set(0, CModelName);
                //create CModel data
                JsonArray CData = new JsonArray();
                Data CModelData = conferencePublications.get(i).getData();
                JsonObject CType = new JsonObject();
                CType.addProperty("type", CModelData.getTypeS());
                CData.set(0, CType);
                JsonObject CSize = new JsonObject();
                CSize.addProperty("size", CModelData.getSize());
                CData.set(1, CSize);
                CModel.set(1, CData);
                //continue model
                JsonObject CModelStatus = new JsonObject();
                CModelStatus.addProperty("status", conferencePublications.get(i).getTested());
                CModel.set(2, CModelStatus);
                JsonObject CResults = new JsonObject();
                CResults.addProperty("results", conferencePublications.get(i).getResults());
                CModel.set(3, CResults);
                publications.add(CModel);
            }
            conferences.set(2, publications);
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

