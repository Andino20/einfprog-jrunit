package einfprog;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;
import plus.einfprog.proxy.ProxyHelper;

import static org.junit.jupiter.api.Assertions.*;

class Bsp10Tests {

    @RegisterExtension
    static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    @Test
    void testPartOne() {
        List.of(
                "Write project proposal",
                "Review pull requests",
                "Write unit tests",
                "Write documentation"
        ).forEach(this::testTask);

        DateProxy d1 = ProxyHelper.create(DateProxy.class, 2022, 12, 1);
        DateProxy d2 = ProxyHelper.create(DateProxy.class, 2022, 12, 2);
        assertEquals(1, d2.compareTo(d1));
        assertEquals(-1, d1.compareTo(d2));
        assertEquals(0, d1.compareTo(d1));

        TimedTaskProxy timedTask = ProxyHelper.create(TimedTaskProxy.class, "Write documentation", d1);
        DateProxy deadline = timedTask.getDeadline();

        assertEquals(d1, deadline); // Equals compares wrapped instances
        assertNotEquals(d2, deadline);

        TaskProxy[] tasks2 = List.of(
                ProxyHelper.create(TaskProxy.class, "Fix login bug"),
                ProxyHelper.create(TaskProxy.class, "Fix registration bug"),
                ProxyHelper.create(TimedTaskProxy.class, "Create presentation",
                        ProxyHelper.create(DateProxy.class, 2022, 6, 26))
        ).toArray(TaskProxy[]::new);
        testTaskList(tasks2);
    }

    private void testTask(String title) {
        TaskProxy task = ProxyHelper.create(TaskProxy.class, title);
        assertEquals(title, task.getTitle());
        assertFalse(task.isDone());

        task.setDone(true);
        assertTrue(task.isDone());

        assertEquals(title, task.toString());
    }

    private void testTaskList(TaskProxy[] tasks) {
        TaskListProxy list = ProxyHelper.create(TaskListProxy.class, tasks.length);

        for (int i = 0; i < tasks.length; i++) {
            TaskProxy t = tasks[i];
            assertNull(list.getTask(i));
            assertNull(list.getTask(tasks[i].getTitle()));
            t.setDone(i % 2 == 0);

            assertTrue(list.addTask(t));
            assertEquals(t, list.getTask(i));
            assertEquals(t, list.getTask(t.getTitle()));
            assertEquals(i + 1, list.getCount());
        }
    }

    private void testAddMultipleTasks(TaskProxy[] tasks) {
        TaskListProxy list = ProxyHelper.create(TaskListProxy.class, tasks.length);
        assertTrue(list.addTasks(tasks));
        assertEquals(tasks.length, list.getCount());
    }

}
