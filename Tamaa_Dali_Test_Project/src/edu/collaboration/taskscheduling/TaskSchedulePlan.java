package edu.collaboration.taskscheduling;

import java.util.LinkedList;
import java.util.Queue;

public class TaskSchedulePlan {
	public Queue<TaskScheduleState> states = new LinkedList<TaskScheduleState>();
	public Queue<TaskScheduleAction> actions = new LinkedList<TaskScheduleAction>();
}
