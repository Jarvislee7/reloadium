package rw.tests.integr;

import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.testFramework.TestActionEvent;
import com.jetbrains.python.run.PythonRunConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rw.action.DebugWithReloadium;
import rw.action.RerunDebugWithReloadium;
import rw.consts.DataKeys;
import rw.tests.BaseTestCase;
import rw.tests.fixtures.CakeshopFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;


public class TestRerunDebugAction extends BaseTestCase {
    CakeshopFixture cakeshop;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        this.cakeshop = new CakeshopFixture(this.getProject());
        this.cakeshop.setUp();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        this.cakeshop.tearDown();

        super.tearDown();
    }

    private AnActionEvent getEventWithConf() {
        ExecutionEnvironment executionEnvironment = spy(ExecutionEnvironment.class);
        lenient().doReturn(this.cakeshop.getSettings()).when(executionEnvironment).getRunnerAndConfigurationSettings();

        AnActionEvent event = spy(TestActionEvent.class);
        lenient().doReturn(executionEnvironment).when(event).getData(DataKeys.EXECUTION_ENVIRONMENT);

        return event;
    }

    @Test
    public void testUpdate() {
        AnAction action = ActionManager.getInstance().getAction(DebugWithReloadium.ID);

        AnActionEvent event = getEventWithConf();
        action.update(event);

        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();
    }

    @Test
    public void testNoExecutionEnv() {
        AnAction action = ActionManager.getInstance().getAction(RerunDebugWithReloadium.ID);

        AnActionEvent event = new TestActionEvent();
        action.update(event);
        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isFalse();
    }

    @Test
    public void testPerform() {
        AnAction action = ActionManager.getInstance().getAction(RerunDebugWithReloadium.ID);

        AnActionEvent event = getEventWithConf();

        action.update(event);
        assertThat(event.getPresentation().isVisible()).isTrue();
        assertThat(event.getPresentation().isEnabled()).isTrue();
        action.actionPerformed(event);

        PythonRunConfiguration runConf = this.cakeshop.getRunConf();

        assertThat(runConf.getScriptName()).isEqualTo("main.py");
        assertThat(runConf.isModuleMode()).isFalse();
        assertThat(runConf.getEnvs().get("PYTHONPATH").isBlank()).isFalse();
        assertThat(runConf.getInterpreterOptions()).isEqualTo("-m reloadium_launcher pydev_proxy");
    }
}
