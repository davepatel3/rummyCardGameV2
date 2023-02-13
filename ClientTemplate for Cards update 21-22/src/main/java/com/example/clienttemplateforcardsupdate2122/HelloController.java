package com.example.clienttemplateforcardsupdate2122;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import socketfx.Constants;
import socketfx.FxSocketClient;
import socketfx.SocketListener;

public class HelloController implements Initializable {
    boolean areReady = false;
    boolean serverReady = false;
    boolean readyDiscard = true;
    boolean firstClickedImageForSwap = true;
    boolean secondClickedImageForSwap = false;
    boolean drawFromDiscard = true;
    boolean drawFromDeck = true;
    @FXML
    private Button sendButton, ready, swap, enableDiscard;
    @FXML
    private TextField sendTextField;
    @FXML
    private Button connectButton;
    @FXML
    private TextField portTextField;
    @FXML
    private TextField hostTextField;
    @FXML
    private Label lblName1, lblName2, lblName3, lblName4, lblMessages, winLabel;

    @FXML
    private GridPane gPaneServer, gPaneClient;

    @FXML
    private ImageView imgS0,imgS1,imgS2,imgS3,imgS4,imgS5,imgS6,imgS7,imgS8,imgS9, imgS10, imgS11, imgS12,imgS13,
            imgC0,imgC1,imgC2,imgC3,imgC4,imgC5,imgC6,imgC7,imgC8,imgC9, imgC10, imgC11, imgC12, imgC13, imgDiscard;
    FileInputStream back1,tempCard;
    Image imageBack;
    Image imageFront;
    List<ImageView> hand1I = new ArrayList<>();
    List<ImageView> hand2I = new ArrayList<>();
    List<Card> hand1D = new ArrayList<>();
    List<Card> hand2D = new ArrayList<>();
    Card discardPileTop;
    Card discardPileTop2;
    int numInServerHand=0;
    private int turn = 0;
    int imgClicked;
    int clientImgClickedForSwap;
    int clientImgClickedForSwap2;

    private final static Logger LOGGER =
            Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private boolean isConnected, serverUNO = false, clientUNO = false;

    public enum ConnectionDisplayState {
        DISCONNECTED, WAITING, CONNECTED, AUTOCONNECTED, AUTOWAITING
    }
    private FxSocketClient socket;
    private void connect() {
        socket = new FxSocketClient(new FxSocketListener(),
                hostTextField.getText(),
                Integer.valueOf(portTextField.getText()),
                Constants.instance().DEBUG_NONE);
        socket.connect();
    }
    private void displayState(ConnectionDisplayState state) {
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isConnected = false;
        displayState(ConnectionDisplayState.DISCONNECTED);
        Runtime.getRuntime().addShutdownHook(new ShutDownThread());
    }

    class ShutDownThread extends Thread {
        @Override
        public void run() {
            if (socket != null) {
                if (socket.debugFlagIsSet(Constants.instance().DEBUG_STATUS)) {
                    LOGGER.info("ShutdownHook: Shutting down Server Socket");
                }
                socket.shutdown();
            }
        }
    }

    public HelloController(){
        try {
            back1 = new FileInputStream("src/main/resources/Images/BACK-1.jpg");
            imageBack = new Image(back1);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
    class FxSocketListener implements SocketListener {

        @Override
        public void onMessage(String line) {
            lblMessages.setText(line);
            if (line.equals("ready") && areReady){
                ready.setVisible(false);
            } else if(line.equals("ready")){
                serverReady=true;
            }
            else if(line.equals("dealt")){


//                imgS0.setImage(imageBack);
//                imgS1.setImage(imageBack);
//                imgS2.setImage(imageBack);
//                imgS3.setImage(imageBack);
//                imgS4.setImage(imageBack);
//                imgS5.setImage(imageBack);
//                imgS6.setImage(imageBack);
                imgS7.setImage(imageBack);

            }else if(line.equals("cCardStart")){
                hand2D.clear();
            }
            else if (line.startsWith("DFD")){
                hand2D.add(new Card(line.substring(3)));

//                discardPileTop2 = new Card(line.substring(3));

//                discardPileTop2 = new Card(line.substring(3));
//                try {
//                    tempCard = new FileInputStream(discardPileTop.getCardPath());
//                    imageFront = new Image(tempCard);
//                }catch (FileNotFoundException e){
//                    e.printStackTrace();
//                }
//
//                imgDiscard.setImage();
            }
            else if (line.startsWith("cCards")){
                hand2D.add(new Card(line.substring(6)));

            } else if(line.startsWith("sCardNum")){
                numInServerHand = Integer.parseInt(line.substring(8));
                for (ImageView x: hand1I){
                    x.setImage(null);
                }
                for (ImageView x: hand2I){
                    x.setImage(null);
                }
                for(int i=0;i<numInServerHand;i++){
                    hand1I.get(i).setImage(imageBack);
                }

                printCCards();


            }
            else if(line.equals("server swapping cards")){
                System.out.println("Server is swapping cards");
            }
            else if(line.equals("server has finished swapping cards for now")){
                System.out.println("Server is done swapping cards");
            }

            else if(line.equals("sdd")){
                drawFromDiscard = true;
                drawFromDeck = true;
                System.out.println("client booleans reset");
            }
            else if(line.equals("YOU HAVE LOST. YOU SUCK AT INDIAN RUMMY")){
                //label gets set to text
            }
            else if(line.equals("CWIC")){
                draw.setDisable(true);
                swap.setDisable(true);
                enableDiscard.setDisable(true);
                winLabel.setText("YOU HAVE WON, CONGRATS");
            }
            else if(line.equals("serverDrawCard")){
                drawFromDeck = true;
                drawFromDiscard = true;
                turn =1;
                draw.setDisable(false);
            }
            else if(line.startsWith("dis")){
                imgDiscard.setImage(null);
                discardPileTop = new Card(line.substring(3));
                try {
                    tempCard = new FileInputStream(discardPileTop.getCardPath());
                    imageFront = new Image(tempCard);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                imgDiscard.setImage(imageFront);
            }
        }

        public void printCCards(){
            for (int i=0;i<hand2D.size();i++){
                try {
                    tempCard = new FileInputStream(hand2D.get(i).getCardPath());
                    imageFront = new Image(tempCard);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                hand2I.get(i).setImage(imageFront);
            }
        }
        @Override
        public void onClosedStatus(boolean isClosed) {

        }
    }
    @FXML
    private void handleReady(ActionEvent event) {

        areReady=true;
        socket.sendMessage("ready");
        if (serverReady){
            ready.setVisible(false);
        }else{
            ready.setDisable(true);
        }

        hand1I.add(imgS0);
        hand1I.add(imgS1);
        hand1I.add(imgS2);
        hand1I.add(imgS3);
        hand1I.add(imgS4);
        hand1I.add(imgS5);
        hand1I.add(imgS6);
        hand1I.add(imgS7);
        hand1I.add(imgS8);
        hand1I.add(imgS9);
        hand1I.add(imgS10);
        hand1I.add(imgS11);
        hand1I.add(imgS12);
        hand1I.add(imgS13);

        hand2I.add(imgC0);
        hand2I.add(imgC1);
        hand2I.add(imgC2);
        hand2I.add(imgC3);
        hand2I.add(imgC4);
        hand2I.add(imgC5);
        hand2I.add(imgC6);
        hand2I.add(imgC7);
        hand2I.add(imgC8);
        hand2I.add(imgC9);
        hand2I.add(imgC10);
        hand2I.add(imgC11);
        hand2I.add(imgC12);
        hand2I.add(imgC13);
    }
    @FXML
    private void handleSwap(ActionEvent event){
        System.out.println(drawFromDeck + "client deck boolean");
        System.out.println(drawFromDiscard + "client discard boolean");

        readyDiscard = false;
        socket.sendMessage("client swapping cards");  //configure server to show this
        if(!readyDiscard){
            draw.setDisable(true);   //remember to enable buttons after swapping
        }
    }

    @FXML
    private void handleEnableDiscard(ActionEvent event){
        readyDiscard = true;
        socket.sendMessage("client has finished swapping cards for now");
        if(readyDiscard){
            draw.setDisable(false);
        }else{
            //nothing at all for no reason
        }
    }

    public void handleDraw(ActionEvent actionEvent) {
        if(drawFromDeck){
            if (turn ==1){
                drawFromDiscard = false;
                draw.setDisable(true);
                socket.sendMessage("clientDrawCard");
            }
        }

    }

    public void handleClientImgClicked(MouseEvent mouseEvent) {
        if(readyDiscard){
            imgClicked = GridPane.getColumnIndex((ImageView) mouseEvent.getSource());
            socket.sendMessage("cic"+ imgClicked);
        }else{
            if(secondClickedImageForSwap){
                clientImgClickedForSwap2 = GridPane.getColumnIndex((ImageView) mouseEvent.getSource());
//
//                firstClickedImageForSwap = true;
                if(!firstClickedImageForSwap && secondClickedImageForSwap){
                    socket.sendMessage("csc" + clientImgClickedForSwap + clientImgClickedForSwap2);   //replace with message from client to server to run there
                }
                firstClickedImageForSwap = true;
                secondClickedImageForSwap = false;

            }else if(firstClickedImageForSwap){
                clientImgClickedForSwap = GridPane.getColumnIndex((ImageView) mouseEvent.getSource());
                firstClickedImageForSwap = false;
                secondClickedImageForSwap = true;

            }
        }
    }

    public void handleClientDiscardClicked(MouseEvent event){
        if(drawFromDiscard){
            if(turn == 1){
                drawFromDeck = false;
                socket.sendMessage("cdd");   //recieve this on server and call clientDrawDiscard method

            }

        }
    }

    @FXML
    Button draw;


    @FXML
    private void handleConnectButton(ActionEvent event) {
        connectButton.setDisable(true);
        displayState(ConnectionDisplayState.WAITING);
        connect();
    }


}