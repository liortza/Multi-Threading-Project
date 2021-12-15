package bgu.spl.mics;

import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Student;

import java.util.ArrayList;

public class Input {
    Student[] Students;
    String[] GPUS;
    int[] CPUS;
    ConfrenceInformation[] Conferences;
    int TickTime, Duration;

    public Input(Student[] Students, String[] GPUS, int[] CPUS, ConfrenceInformation[] Conferences, int TickTime, int Duration) {
        this.Students = Students;
        this.GPUS = GPUS;
        this.CPUS = CPUS;
        this.Conferences = Conferences;
        this.TickTime = TickTime;
        this.Duration = Duration;
    }

    public Student[] getStudents() { return Students; }

    public ConfrenceInformation[] getConferences() { return Conferences; }

    public String[] getGpus() { return GPUS; }

    public int[] getCpus() { return CPUS; }

    public int getTickTime() { return TickTime; }

    public int getDuration() { return Duration; }
}
