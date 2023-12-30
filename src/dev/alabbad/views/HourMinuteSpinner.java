package dev.alabbad.views;

import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import static javafx.scene.input.MouseButton.PRIMARY;

/**
 * A spinner control for the time.
 *
 * source:
 * - https://github.com/hemeroc/javafx-datetimepicker
 *
 * @author Dominik Moser (https://github.com/hemeroc)
 * @version x.x.x
 */
class HourMinuteSpinner extends HBox {

    private final SpinnerValueFactory.IntegerSpinnerValueFactory integerSpinnerValueFactory;

    ObjectProperty<Integer> valueProperty() {
        return integerSpinnerValueFactory.valueProperty();
    }

    public HourMinuteSpinner(int min, int max, int initial) {
        // Create a Spinner Value Factory for integers with min, max, and initial values
        integerSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initial);
        // Enable wrap-around behaviour
        integerSpinnerValueFactory.setWrapAround(true);

        // Set the style class of the custom spinner control
        this.getStyleClass().add("spinner");

        // Create the decrease button
        Button decreaseButton = new Button();
        decreaseButton.getStyleClass().add("left-button");
        StackPane decreaseArrow = new StackPane();
        decreaseArrow.getStyleClass().add("left-arrow");
        decreaseArrow.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        decreaseButton.setGraphic(decreaseArrow);

        // Create the increase button
        Button increaseButton = new Button();
        increaseButton.getStyleClass().add("right-button");
        StackPane increaseArrow = new StackPane();
        increaseArrow.getStyleClass().add("right-arrow");
        increaseArrow.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        increaseButton.setGraphic(increaseArrow);

        // Create a label to display the selected value (formatted as "%02d")
        Label valueLabel = new Label();
        valueLabel.setMinWidth(20);
        valueLabel.textProperty().bind(Bindings.format("%02d", integerSpinnerValueFactory.valueProperty()));
        valueLabel.getStyleClass().add("spinner-label");

        // Create an increase handler for both buttons
        IncrementHandler incrementHandler = new IncrementHandler(integerSpinnerValueFactory);

        // Set up event handleres for the decrease button
        decreaseButton.setOnAction(event -> integerSpinnerValueFactory.decrement(1));
        decreaseButton.addEventFilter(MouseEvent.MOUSE_PRESSED, incrementHandler);
        decreaseButton.addEventFilter(MouseEvent.MOUSE_RELEASED, incrementHandler);

        // Set up event handlers for the increase button
        increaseButton.setOnAction(event -> integerSpinnerValueFactory.increment(1));
        increaseButton.addEventFilter(MouseEvent.MOUSE_PRESSED, incrementHandler);
        increaseButton.addEventFilter(MouseEvent.MOUSE_RELEASED, incrementHandler);

        // Add components to the custom spinner control
        this.getChildren().addAll(decreaseButton, valueLabel, increaseButton);
    }

    private static final class IncrementHandler implements EventHandler<MouseEvent> {
        private SpinnerValueFactory spinnerValueFactory;
        private boolean increment;
        private long startTimestamp;
        private long nextStep;
        private static final long DELAY = 1000L * 1000L * 500; // 0.5 sec
        private static final long STEP = 1000L * 1000L * 100; // 0.5 sec
        private final AnimationTimer timer = new AnimationTimer() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void handle(long now) {
                if (now - startTimestamp >= nextStep) {
                    nextStep += STEP;
                    if (increment) {
                        spinnerValueFactory.increment(1);
                    } else {
                        spinnerValueFactory.decrement(1);
                    }
                }
            }
        };

        IncrementHandler(SpinnerValueFactory.IntegerSpinnerValueFactory integerSpinnerValueFactory) {
            spinnerValueFactory = integerSpinnerValueFactory;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void handle(MouseEvent event) {
            if (event.getButton() == PRIMARY) {
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    Button button = (Button) event.getSource();
                    increment = button.getStyleClass().contains("right-button");
                    startTimestamp = System.nanoTime();
                    nextStep = DELAY;
                    timer.handle(startTimestamp + DELAY);
                    timer.start();
                    event.consume();
                    button.requestFocus();
                }
                if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                    timer.stop();
                }
            }
        }
    }
}
