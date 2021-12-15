package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.io.FileWriter;
import java.io.IOException;
import bgu.spl.mics.Input;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.ConferenceService;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.StudentService;
import com.google.gson.Gson;

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

            for (GPU gpu : input.getGpus()) {
                GPUService gpuService = new GPUService(gpu.getName(), gpu);
                Thread gpuT = new Thread(gpuService);
                gpuT.start();
            }

            for (CPU cpu : input.getCpus()) {
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

        } catch (IOException e) {
            System.out.println("caught exception");
        }
        // endregion
        Output();

    }

    private static void Output() {
        JSONArray Output = new JSONArray();
        JSONArray students = new JSONArray();
        Output.set(0, students);
        JSONArray conferences = new JSONArray();
        Output.set(1, conferences);
        JSONObject cpuTimeUsed = new JSONObject();
        cpuTimeUsed.put("cpuTimeUsed", cluster.cpuTicksUsed());
        Output.set(2, cpuTimeUsed);
        JSONObject gpuTimeUsed = new JSONObject();
        gpuTimeUsed.put("gpuTimeUsed", cluster.gpuTicksUsed());
        Output.set(3, gpuTimeUsed);
        JSONObject batchesProcessed = new JSONObject();
        batchesProcessed.put("batchesProcessed", cluster.cpuTotalProcessedBatches());
        Output.set(4, batchesProcessed);
        //build Students
        for (int i = 0; i < CRMSRunner.students.length; i++) {
            //build student
            JSONArray student = new JSONArray();
            //insert student data
            JSONObject name = new JSONObject();
            name.put("name", CRMSRunner.students[i].getName());
            student.set(0, name);
            JSONObject department = new JSONObject();
            department.put("department", CRMSRunner.students[i].getDepartment());
            student.set(1, department);
            JSONObject status = new JSONObject();
            status.put("status", CRMSRunner.students[i].getStatus());
            student.set(2, status);
            JSONObject publications = new JSONObject();
            publications.put("publications", CRMSRunner.students[i].getStatus());
            student.set(3, publications);
            JSONObject papersRead = new JSONObject();
            papersRead.put("papersRead", CRMSRunner.students[i].getNumOfPapers());
            student.set(4, papersRead);
            //models
            JSONArray trainedModels = new JSONArray();
            Model[] studentModels = CRMSRunner.students[i].getModels();
            for (int j = 0; j < studentModels.length; j++) {
                if (studentModels[i].isTrained()) {
                    //create model
                    JSONArray model = new JSONArray();
                    JSONObject modelName = new JSONObject();
                    modelName.put("name", studentModels[i].getName());
                    model.set(0, modelName);
                    //create model data
                    JSONArray data = new JSONArray();
                    Data ModelData = studentModels[i].getData();
                    JSONObject type = new JSONObject();
                    type.put("type", ModelData.getTypeS());
                    data.set(0, type);
                    JSONObject size = new JSONObject();
                    size.put("size", ModelData.getSize());
                    data.set(1, size);
                    model.set(1, data);
                    //continue model
                    JSONObject modelStatus = new JSONObject();
                    modelStatus.put("status", studentModels[i].getStatus());
                    model.set(2, modelName);
                    JSONObject results = new JSONObject();
                    results.put("results", studentModels[i].getResults());
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
            JSONObject conferenceName = new JSONObject();
            conferenceName.put("name", CRMSRunner.conferences[i].getName());
            conferences.set(0, conferenceName);
            JSONObject conferenceDate = new JSONObject();
            conferenceDate.put("Date", CRMSRunner.conferences[i].getDate());
            conferences.set(1, conferenceDate);
            //conference publications
            JSONArray publications = new JSONArray();
            LinkedList<Model> conferencePublications = CRMSRunner.conferences[i].getModels();
            for (int j = 0; i < conferencePublications.size(); i++) {
                //create model
                JSONArray CModel = new JSONArray();
                JSONObject CModelName = new JSONObject();
                CModelName.put("name", conferencePublications.get(i).getName());
                CModel.set(0, CModelName);
                //create CModel data
                JSONArray CData = new JSONArray();
                Data CModelData = conferencePublications.get(i).getData();
                JSONObject CType = new JSONObject();
                CType.put("type", CModelData.getTypeS());
                CData.set(0, CType);
                JSONObject CSize = new JSONObject();
                CSize.put("size", CModelData.getSize());
                CData.set(1, CSize);
                CModel.set(1, CData);
                //continue model
                JSONObject CModelStatus = new JSONObject();
                CModelStatus.put("status", conferencePublications.get(i).getStatus());
                CModel.set(2, CModelStatus);
                JSONObject CResults = new JSONObject();
                CResults.put("results", conferencePublications.get(i).getResults());
                CModel.set(3, CResults);
                publications.add(CModel);
            }
            conferences.set(2, publications);
        }
        try {
            FileWriter file = new FileWriter("E:/output.json");
            file.write(Output.toJSONString());
            file.close();
        } catch (IOException e) {
        }
        System.out.println("JSON file created: " + Output);
    }
}

