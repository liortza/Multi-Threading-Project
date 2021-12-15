package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    static Student[] Students;
    static ConfrenceInformation[] Conferences;
    static Cluster cluster = new Cluster();

    public static void main(String[] args) {
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
        for (int i = 0; i < Students.length; i++) {
            //build student
            JSONArray student = new JSONArray();
            //insert student data
            JSONObject name = new JSONObject();
            name.put("name", Students[i].getName());
            student.set(0, name);
            JSONObject department = new JSONObject();
            department.put("department", Students[i].getDepartment());
            student.set(1, department);
            JSONObject status = new JSONObject();
            status.put("status", Students[i].getStatus());
            student.set(2, status);
            JSONObject publications = new JSONObject();
            publications.put("publications", Students[i].getStatus());
            student.set(3, publications);
            JSONObject papersRead = new JSONObject();
            papersRead.put("papersRead", Students[i].getNumOfPapers());
            student.set(4, papersRead);
            //models
            JSONArray trainedModels = new JSONArray();
            Model[] studentModels = Students[i].getModels();
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
        for(int i=0; i<Conferences.length; i++){
            JSONObject conferenceName = new JSONObject();
            conferenceName.put("name", Conferences[i].getName());
            conferences.set(0, conferenceName);
            JSONObject conferenceDate = new JSONObject();
            conferenceDate.put("Date", Conferences[i].getDate());
            conferences.set(1, conferenceDate);
            //conference publications
            JSONArray publications= new JSONArray();
            LinkedList<Model> conferencePublications=Conferences[i].getModels();
            for(int j=0; i<conferencePublications.size(); i++){
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
