package application;

import javafx.application.Application;
import javafx.scene.* ; // Scene, Group, Node, etc.
import javafx.scene.text.* ; // Text, Font
import javafx.scene.paint.* ; // Paint, Color
import javafx.stage.Stage;
import javafx.scene.layout.* ;
import javafx.scene.control.* ; // Slider, Button, Label
import javafx.geometry.* ; // Pos, Insets, Point2D, Orientation
import javafx.scene.shape.* ; // Rectangle, Circle, etc.
import javafx.beans.property.* ; // DoubleProperty, IntegerProperty etc.
import javafx.beans.binding.ObjectBinding ;
import javafx.event.ActionEvent;


public class Prueba extends Application
{
   // A StackPane will be the root pane for all visible things on the screen.

   StackPane root_stack = new StackPane() ;

   //  The word WINDOW in the following constants refers to the 'window'
   //  that is drawn onto the screen by this program.

   static final Point2D WINDOW_UPPER_LEFT_CORNER  =  new  Point2D( 210, 50 ) ;
   static final int WINDOW_WIDTH  =  400, WINDOW_HEIGHT  =  500 ;

   Slider left_curtain_slider  =  new  Slider( 10, WINDOW_WIDTH / 2 + 10,
                                               WINDOW_WIDTH / 4 ) ;

   Slider right_curtain_slider  =  new  Slider( 10, WINDOW_WIDTH / 2 + 10,
                                                WINDOW_WIDTH / 4 ) ;

   IntegerProperty red_color_rgb_value   = new SimpleIntegerProperty( 255 ) ;
   IntegerProperty green_color_rgb_value = new SimpleIntegerProperty( 50 ) ;
   IntegerProperty blue_color_rgb_value  = new SimpleIntegerProperty( 255 ) ;

   DoubleProperty  alpha_value = new SimpleDoubleProperty( 0.95 ) ;


   Label red_color_label   = new Label(
               String.format( "RED:  %d", red_color_rgb_value.getValue() ) ) ;
   Label green_color_label = new Label(
               String.format( "GREEN: %d", green_color_rgb_value.getValue() ) ) ;
   Label blue_color_label  = new Label(
               String.format( "BLUE:  %d", blue_color_rgb_value.getValue() ) ) ;
   Label alpha_value_label = new Label(
               String.format( "ALPHA: %.2f", alpha_value.getValue() ) ) ;



   public void start( Stage stage )
   {
      stage.setTitle( "CurtainsFX.java" ) ;

      Rectangle blue_sky = new Rectangle( WINDOW_UPPER_LEFT_CORNER.getX(),
                                          WINDOW_UPPER_LEFT_CORNER.getY(),
                                          WINDOW_WIDTH, WINDOW_HEIGHT / 2 ) ;
      blue_sky.setFill( Color.LIGHTSKYBLUE ) ;

      Rectangle green_ground = new Rectangle( WINDOW_UPPER_LEFT_CORNER.getX(),
                         WINDOW_UPPER_LEFT_CORNER.getY() + WINDOW_HEIGHT / 2,
                         WINDOW_WIDTH, WINDOW_HEIGHT / 2 ) ;

      green_ground.setFill( Color.SPRINGGREEN ) ;


      Circle golden_sun = new Circle( WINDOW_UPPER_LEFT_CORNER.getX() + 96,
                                      WINDOW_UPPER_LEFT_CORNER.getY() + 80,
                                      32, Color.GOLD ) ;

      Rectangle window_frame = new Rectangle( WINDOW_UPPER_LEFT_CORNER.getX(),
                                              WINDOW_UPPER_LEFT_CORNER.getY(),
                                              WINDOW_WIDTH, WINDOW_HEIGHT ) ;
      window_frame.setFill( Color.TRANSPARENT ) ;
      window_frame.setStroke( Color.BLACK ) ;

      Line curtain_rod = new Line( WINDOW_UPPER_LEFT_CORNER.getX() - 10,
                                   WINDOW_UPPER_LEFT_CORNER.getY() - 10,
                                   WINDOW_UPPER_LEFT_CORNER.getX() + WINDOW_WIDTH + 10,
                                   WINDOW_UPPER_LEFT_CORNER.getY() - 10 ) ;

      curtain_rod.setStroke( Color.FIREBRICK ) ;
      curtain_rod.setStrokeWidth( 4 ) ;

      Rectangle left_curtain = new Rectangle( WINDOW_UPPER_LEFT_CORNER.getX() - 10,
                                              WINDOW_UPPER_LEFT_CORNER.getY() - 10,
                                              0,
                                              WINDOW_HEIGHT + 20 ) ;

      left_curtain.widthProperty().bind( left_curtain_slider.valueProperty() ) ;

      Rectangle right_curtain = new Rectangle( WINDOW_UPPER_LEFT_CORNER.getX()
                                               + WINDOW_WIDTH + 10
                                               - right_curtain_slider.getValue(),
                                               WINDOW_UPPER_LEFT_CORNER.getY() - 10,
                                               0,
                                               WINDOW_HEIGHT + 20 ) ;

      right_curtain.widthProperty().bind( right_curtain_slider.valueProperty() ) ;

      // Because the x-coordinate of the rectangle that depicts the right
      // curtain depends on the width of the curtain, we must create a separate
      // listening mechanism to set the x-coordinate.

      right_curtain_slider.valueProperty().addListener(

         ( observable_value, value, new_value ) ->
         {
            right_curtain.setX( WINDOW_UPPER_LEFT_CORNER.getX() + WINDOW_WIDTH + 10
                                - right_curtain_slider.getValue() ) ;
         }
      ) ;

      //  Next we specify how the color of the curtains depends on the values
      //  that can be set through the buttons.

      ObjectBinding<Paint> curtains_color_binding = new ObjectBinding<Paint>()
      {
         {
             super.bind( red_color_rgb_value, green_color_rgb_value,
                         blue_color_rgb_value, alpha_value ) ;
         }

         @Override
         protected Paint computeValue()
         {
             return Color.rgb( red_color_rgb_value.getValue(),
                               green_color_rgb_value.getValue(),
                               blue_color_rgb_value.getValue(),
                               alpha_value.getValue() ) ;
         }
      };

      left_curtain.fillProperty().bind( curtains_color_binding ) ;
      right_curtain.fillProperty().bind( curtains_color_binding ) ;

      right_curtain_slider.setRotate( 180 ) ;  // This 'inverts' the slider.


      Group group_for_curtains_etc = new Group() ;

      // With the following statement we disable the automatic layout
      // management of the Card objects.
      group_for_curtains_etc.setManaged( false ) ;

      group_for_curtains_etc.getChildren().addAll( blue_sky, green_ground,
                                                   golden_sun, window_frame,
                                                   curtain_rod,
                                                   left_curtain, right_curtain ) ;

      // Now we have built the window and the curtains that hang over
      // the window. The following task is to set up the GUI controls.


      // The colors of the texts inside the RED, GREEN, and BLUE labels
      // will depend on the RGB value of the color in question.

      red_color_label.setTextFill(
                    Color.rgb( red_color_rgb_value.getValue(), 0, 0 ) ) ;
      green_color_label.setTextFill(
                    Color.rgb( 0, green_color_rgb_value.getValue(), 0 ) ) ;
      blue_color_label.setTextFill(
                    Color.rgb( 0, 0, blue_color_rgb_value.getValue() ) ) ;

      Button red_color_decrease_button = new Button( "<" ) ;
      Button red_color_increase_button = new Button( ">" ) ;
      Button green_color_decrease_button = new Button( "<" ) ;
      Button green_color_increase_button = new Button( ">" ) ;
      Button blue_color_decrease_button = new Button( "<" ) ;
      Button blue_color_increase_button = new Button( ">" ) ;
      Button alpha_value_decrease_button = new Button( "<" ) ;
      Button alpha_value_increase_button = new Button( ">" ) ;


      // With the following statements we create unidirectional bindings
      // so that the textFill property of the Button objects is bound
      // to the corresponding property of the Label object. When the text
      // color of a Label changes, the text colors of corresponding Buttons
      // will change automatically.

      red_color_decrease_button.textFillProperty().bind(
                                      red_color_label.textFillProperty() ) ;

      red_color_increase_button.textFillProperty().bind(
                                      red_color_label.textFillProperty() ) ;

      green_color_decrease_button.textFillProperty().bind(
                                      green_color_label.textFillProperty() ) ;

      green_color_increase_button.textFillProperty().bind(
                                      green_color_label.textFillProperty() ) ;

      blue_color_decrease_button.textFillProperty().bind(
                                      blue_color_label.textFillProperty() ) ;

      blue_color_increase_button.textFillProperty().bind(
                                      blue_color_label.textFillProperty() ) ;


      // Next, we'll specify the actions that will be performed when the
      // buttons for the adjustment of red color are pressed.

      red_color_decrease_button.setOnAction( ( ActionEvent event ) ->
      {
         if ( red_color_rgb_value.getValue() >= 5 )
         {
            red_color_rgb_value.setValue( red_color_rgb_value.getValue() - 5 ) ;
         }
      } ) ;

      red_color_increase_button.setOnAction( ( ActionEvent event ) ->
      {
         if ( red_color_rgb_value.getValue() <= 250 )
         {
            red_color_rgb_value.setValue( red_color_rgb_value.getValue() + 5 ) ;
         }
      } ) ;


      // Next, we'll specify what will happen when the value of red color
      // has been altered with the two buttons.

      red_color_rgb_value.addListener(

         ( observable_value, value, new_value ) ->
         {
            red_color_label.setText( String.format(
                               "RED:  %d", red_color_rgb_value.getValue() ) ) ;

            red_color_label.setTextFill(
                    Color.rgb( red_color_rgb_value.getValue(), 0, 0 ) ) ;

         }
      ) ;


      // The actions specified for the color buttons are almost identical.

      green_color_decrease_button.setOnAction( ( ActionEvent event ) ->
      {
         if ( green_color_rgb_value.getValue() >= 5 )
         {
            green_color_rgb_value.setValue( green_color_rgb_value.getValue() - 5 ) ;
         }
      } ) ;

      green_color_increase_button.setOnAction( ( ActionEvent event ) ->
      {
         if ( green_color_rgb_value.getValue() <= 250 )
         {
            green_color_rgb_value.setValue( green_color_rgb_value.getValue() + 5 ) ;
         }
      } ) ;


      green_color_rgb_value.addListener(

         ( observable_value, value, new_value ) ->
         {
            green_color_label.setText( String.format(
                               "GREEN:  %d", green_color_rgb_value.getValue() ) ) ;

            green_color_label.setTextFill(
                    Color.rgb( 0, green_color_rgb_value.getValue(), 0 ) ) ;

         }
         
      ) ;

      blue_color_decrease_button.setOnAction( ( ActionEvent event ) ->
      {
         if ( blue_color_rgb_value.getValue() >= 5 )
         {
            blue_color_rgb_value.setValue( blue_color_rgb_value.getValue() - 5 ) ;
         }
      } ) ;

      blue_color_increase_button.setOnAction( ( ActionEvent event ) ->
      {
         if ( blue_color_rgb_value.getValue() <= 250 )
         {
            blue_color_rgb_value.setValue( blue_color_rgb_value.getValue() + 5 ) ;
         }
      } ) ;


      blue_color_rgb_value.addListener(

         ( observable_value, value, new_value ) ->
         {
            blue_color_label.setText( String.format(
                               "BLUE:  %d", blue_color_rgb_value.getValue() ) ) ;

            blue_color_label.setTextFill(
                    Color.rgb( 0, 0, blue_color_rgb_value.getValue() ) ) ;

         }
      ) ;

      alpha_value_decrease_button.setOnAction( ( ActionEvent event ) ->
      {
         if ( alpha_value.getValue() >= 0.05 )
         {
            alpha_value.setValue( alpha_value.getValue() - 0.05 ) ;
         }
      } ) ;

      alpha_value_increase_button.setOnAction( ( ActionEvent event ) ->
      {
         if ( alpha_value.getValue() <= 0.95 )
         {
            alpha_value.setValue( alpha_value.getValue() + 0.05 ) ;
         }
      } ) ;


      alpha_value.addListener(

         ( observable_value, value, new_value ) ->
         {
            alpha_value_label.setText( String.format(
                               "ALPHA:  %.2f", alpha_value.getValue() ) ) ;

         }
      ) ;


      HBox curtain_adjustments_pane = new HBox( 16 ) ; 

      curtain_adjustments_pane.getChildren().addAll( left_curtain_slider,
                                                      right_curtain_slider ) ;

      curtain_adjustments_pane.setAlignment( Pos.CENTER ) ; // The Box is centered
      // With an Insets object we can specify empty space around the HBox.
      // There will be 20 pixels padding below the HBox.
      curtain_adjustments_pane.setPadding( new Insets( 0, 0, 20, 0 ) ) ;


      HBox color_adjustments_pane = new HBox( 16 ) ; 

      color_adjustments_pane.getChildren().addAll(
         red_color_label, red_color_decrease_button, red_color_increase_button,
         green_color_label, green_color_decrease_button, green_color_increase_button,
         blue_color_label, blue_color_decrease_button, blue_color_increase_button,
         alpha_value_label, alpha_value_decrease_button, alpha_value_increase_button ) ;

      color_adjustments_pane.setAlignment( Pos.CENTER ) ; // The Box is centered
      // With an Insets object we can specify empty space around the HBox.
      // There will be 20 pixels padding below the HBox.
      color_adjustments_pane.setPadding( new Insets( 0, 0, 20, 0 ) ) ;


      VBox operations_pane = new VBox() ;

      operations_pane.getChildren().addAll( curtain_adjustments_pane,
                                            color_adjustments_pane ) ;

      BorderPane border_pane = new BorderPane() ;

      border_pane.setBottom( operations_pane ) ;



      root_stack.getChildren().addAll( border_pane,
                                       group_for_curtains_etc ) ;

      Scene scene = new Scene( root_stack, 820, 720 ) ;


      // By eliminating the background specifications of the StackPane,
      // we can make the fill of the Scene visible.

      root_stack.setBackground( null ) ;

      scene.setFill( Color.IVORY ) ; // IVORY is light yellowish color

      stage.setScene( scene ) ;
      stage.show() ;

   }

   public static void main( String[] command_line_parameters )
   {
      launch( command_line_parameters ) ;
   }
}


/*  NOTES:


The following were useful pages in developing this program:

https://docs.oracle.com/javase/8/javafx/api/javafx/scene/layout/HBox.html

http://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Slider.html

https://docs.oracle.com/javase/8/javafx/api/javafx/scene/layout/StackPane.html

*/

