package io.github.rowak.nanoleafdesktop.ui.dialog;

import io.github.rowak.nanoleafdesktop.IListenToMessages;
import org.junit.Test;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfirmationActionListenerShould {

    @Test
    public void notify_about_error_when_host_not_reachable() {
        ParentSpy parent = new ParentSpy();
        TestableConfirmationActionListener confirmationActionListener = createConfirmationActionListenerForCase(
                "IOException", parent);

        confirmationActionListener.actionPerformed(null);

        assertThat(parent.dialogCreated).extracting(IDeliverMessages::getMessage).isEqualTo("Failed to automatically redirect. Go to myRepo to download the update.");
    }

    private TestableConfirmationActionListener createConfirmationActionListenerForCase(
            String useCase, ParentSpy listenToMessagesSpy) {
        TestableConfirmationActionListener confirmationActionListener = new TestableConfirmationActionListener(
                listenToMessagesSpy, "myRepo");
        confirmationActionListener.setUseCase(useCase);
        return confirmationActionListener;
    }

    @Test
    public void notify_about_error_when_host_is_malformed() {
        ParentSpy parent = new ParentSpy();
        TestableConfirmationActionListener confirmationActionListener = createConfirmationActionListenerForCase(
                "URISyntaxException", parent);

        confirmationActionListener.actionPerformed(null);

        assertThat(parent.dialogCreated).extracting(IDeliverMessages::getMessage).isEqualTo("An internal error occurred. The update cannot be completed.");
    }

    @Test
    public void notify_about_error_when_desktop_is_not_supported() {
        ParentSpy parent = new ParentSpy();
        TestableConfirmationActionListener confirmationActionListener = createConfirmationActionListenerForCase(
                "desktopNotSupported", parent);

        confirmationActionListener.actionPerformed(null);

        assertThat(parent.dialogCreated).extracting(IDeliverMessages::getMessage).isEqualTo("Failed to automatically redirect. Go to myRepo to download the update.");
    }

    private class TestableConfirmationActionListener extends ConfirmationActionListener {
        private String exception;
        private boolean isDesktopSupported = true;

        public TestableConfirmationActionListener(IListenToMessages listenToMessagesStub, String url) {
            super(listenToMessagesStub, url);
        }

        @Override
        protected boolean desktopIsSupported() {
            return isDesktopSupported;
        }

        @Override
        protected void actOnEvent(ActionEvent e) {
        }

        @Override
        protected void callRepo() throws IOException, URISyntaxException {
            switch (exception) {
                case "IOException":
                    throw new IOException();
                case "URISyntaxException":
                    throw new URISyntaxException("A", "B");
                default:
            }
        }

        public void setUseCase(String useCase) {
            switch (useCase) {
                case "desktopNotSupported":
                    isDesktopSupported = false;
                    exception = "";
                    break;
                default:
                    isDesktopSupported = true;
                    exception = useCase;
            }
        }
    }

    private class ParentSpy implements IListenToMessages {
        public IDeliverMessages dialogCreated;

        @Override
        public void render(UpdateOptionDialog updateDialog) {

        }

        @Override
        public void createDialog(IDeliverMessages message) {
            dialogCreated = message;
        }
    }
}
