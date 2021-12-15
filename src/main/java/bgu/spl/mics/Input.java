package bgu.spl.mics;

import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Student;

import java.util.ArrayList;

public class Input {
    Student[] Students;
    String[] GPUS;
    ArrayList<GPU> gpus = new ArrayList<>();
    int[] CPUS;
    ArrayList<CPU> cpus = new ArrayList<>();
    ConfrenceInformation[] Conferences;
    int TickTime, Duration;

    public Input(Student[] Students, String[] GPUS, int[] CPUS, ConfrenceInformation[] Conferences, int TickTime, int Duration) {
        this.Students = Students;
        this.GPUS = GPUS;
        for (String type: GPUS) gpus.add(new GPU(type));
        this.CPUS = CPUS;
        for (int cores : CPUS) cpus.add(new CPU(cores));
        this.Conferences = Conferences;
        this.TickTime = TickTime;
        this.Duration = Duration;
    }

    public Student[] getStudents() { return Students; }

    public ConfrenceInformation[] getConferences() { return Conferences; }

    public ArrayList<GPU> getGpus() { return gpus; }

    public ArrayList<CPU> getCpus() { return cpus; }
}
