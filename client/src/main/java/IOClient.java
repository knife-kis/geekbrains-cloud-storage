import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class IOClient extends Application {

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String clientPath = "C:\\Users\\mlev1219\\IdeaProjects\\geekbrains-cloud-storage-june\\client\\src\\main\\resources\\clientPath";

    public IOClient() throws HeadlessException, IOException {

        socket = new Socket("localhost", 8189);

        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        JTextField txt = new JTextField();
        JButton send = new JButton("send");
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(txt);
        panel.add(send);
        send.addActionListener(action -> {
            String fileName = txt.getText();
            try {
                File file = new File(clientPath + "\\" + fileName);
                if (!file.exists()) {
                    txt.setText("FILE WITH NAME " + fileName + " NOT EXISTS IN DIRECTORY");
                } else {
                    out.writeUTF("./send");
                    out.flush();
                    out.writeUTF(fileName);
                    out.flush();
                    long length = file.length();
                    out.writeLong(length);
                    out.flush();
                    FileInputStream fis = new FileInputStream(file);
                    byte [] buffer = new byte[1024];
                    while (fis.available() > 0) {
                        int read = fis.read(buffer);
                        out.write(buffer, 0, read);
                        out.flush();
                    }
                    String callBack = in.readUTF();
                    txt.setText(callBack);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        primaryStage.setTitle("Storage");
        primaryStage.setScene(new Scene(root, 350, 600));
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException {
        new IOClient();
        launch(args);
    }
}