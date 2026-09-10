package einfprog;

import plus.einfprog.proxy.Proxy;

@Proxy("einfprog.TaskList")
public interface TaskListProxy {

    int getCapacity();

    int getCount();

    boolean addTask(TaskProxy task);

    TaskProxy addTask(String title);

    boolean addTasks(TaskProxy[] tasks);

    TaskProxy getTask(String title);

    TaskProxy getTask(int idx);

    TaskProxy removeTask(String title);

    TaskListProxy getTODOsDueUntil(DateProxy deadline);

    boolean compact();

    int foo();
}
