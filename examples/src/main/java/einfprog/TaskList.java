package einfprog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.OptionalInt;

public class TaskList {
    final Task[] tasks;

    /**
     * creates a new TaskList with the given maximum capacity;
     * if capacity is smaller than zero, the capacity is set to 0
     */
    public TaskList(int capacity) {
        tasks = new Task[Math.max(capacity, 0)];
    }

    /**
     * returns the maximum capacity
     */
    public int getCapacity() {
        return tasks.length;
    }

    /**
     * returns the number of actual (non-null) Task objects managed by the tasks array
     */
    public int getCount() {
        return (int) Arrays.stream(tasks)
                .filter(Objects::nonNull)
                .count();
    }

    /**
     * adds the given task to the tasks array at the first free position; returns true if the task was added successfully;
     * otherwise returns false (i.e., the list has already reached the maximum capacity,
     * or another task with the same title already exists, or task is `null` or the task's title is `null`)
     */
    public boolean addTask(Task task) {
        if (task == null) {
            return false;
        }

        if (task.getTitle() == null) { // TODO: Inform Luis that this case can not happend because we can't test for null-checks in Task constructor
            return false;
        }

        OptionalInt freePos = OptionalInt.empty();
        for (int i = tasks.length - 1; i >= 0; i--) {
            if (tasks[i] == null) {
                freePos = OptionalInt.of(i);
            } else if (tasks[i].getTitle().equals(task.getTitle())) {
                return false;
            }
        }

        freePos.ifPresent(i -> tasks[i] = task);
        return freePos.isPresent();
    }

    /**
     * adds a newly created `Task` with the given title to the tasks array at the first free position;
     * returns the created task if the task was added successfully; otherwise returns null (i.e., the list has
     * already reached the maximum capacity, or another task with the same title already exists, or the title is `null`)
     */
    public Task addTask(String title) {
        Task t = new Task(title);
        if (!addTask(t)) {
            return null;
        }
        return t;
    }

    /**
     * adds all given tasks to the tasks array (the order is retained; every task is added at the first free position);
     * returns true if all the tasks were added successfully; otherwise returns false (see `addTask(Task task)`)
     */
    public boolean addTasks(Task[] tasks) {
        return Arrays.stream(tasks).allMatch(this::addTask);
    }

    /**
     * returns the task with the specified title, or `null` if no such task was found
     */
    public Task getTask(String title) {
        for (Task t : tasks) {
            if (t != null && t.getTitle().equals(title)) {
                return t;
            }
        }
        return null;
    }

    /**
     * returns the task at the specified index in the tasks array;
     * returns null if there is no task at this index, or the index is invalid (i.e., out-of-bounds).
     */
    public Task getTask(int idx) {
        if (idx < 0 || idx >= tasks.length) {
            return null;
        }
        return tasks[idx];
    }

    public Task removeTask(String title) {
        for (int i = 0; i < tasks.length; i++) {
            if (tasks[i] != null && tasks[i].getTitle().equals(title)) {
                Task t = tasks[i];
                tasks[i] = null;
                return t;
            }
        }
        return null;
    }

    public TaskList getTODOsDueUntil(Date deadline) {
        ArrayList<Task> included = new ArrayList<>();
        for (Task t : tasks) {
            if (!t.isDone()) {
                if (t instanceof TimedTask task) {
                    if (task.getDeadline().compareTo(deadline) <= 0) {
                        included.add(task);
                    }
                } else {
                    included.add(t);
                }
            }
        }
        Task[] includedTasks = included.toArray(Task[]::new);
        TaskList newList = new TaskList(this.getCapacity());
        newList.addTasks(includedTasks);
        return newList;
    }

    /**
     * prints the string representation (as obtained by `toString()`) of all tasks in the order of the tasks array;
     * null-values are ignored. Tasks that are marked as done are only printed if `includeDone` is `true`
     */
    public void printTasks(boolean includeDone) {
        for (Task task : tasks) {
            if (task != null && (!task.isDone() || includeDone)) {
                System.out.println(task);
            }
        }
    }

    public boolean compact() {
        boolean changed = false;
        for (int i = 0; i < tasks.length; i++) {
            if (tasks[i] == null) {
                for (int j = i + 1; j < tasks.length; j++) {
                    if (tasks[j] != null) {
                        tasks[i] = tasks[j];
                        tasks[j] = null;
                        changed = true;
                        break;
                    }
                }
            }
        }
        return changed;
    }


    public int foo() {
        return 5;
    }
}