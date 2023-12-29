package dev.alabbad.views;

// source:
// - https://github.com/hemeroc/javafx-datetimepicker

// For View
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Skin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;

// For Skin
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// For CustomBinding
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import java.util.function.Function;

import dev.alabbad.exceptions.ParseValueException;
import dev.alabbad.interfaces.IInputControl;
import dev.alabbad.utils.Parser;

public class DateTimePicker extends DatePicker implements IInputControl<String> {
    private ObjectProperty<LocalDateTime> dateTimeValue = new SimpleObjectProperty<>(LocalDateTime.now());
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_FORMAT);
    private ObjectProperty<String> format = new SimpleObjectProperty<>() {
        public void set(String newValue) {
            super.set(newValue);
            formatter = DateTimeFormatter.ofPattern(newValue);
        }
    };
    public static final String DEFAULT_FORMAT = "dd/MM/yyyy HH:mm";
    private Function<String> parsor;
    private String parsedVal;

    public DateTimePicker(LocalDateTime localDateTime) {
        super(localDateTime.toLocalDate());
        getStyleClass().add("date-time-picker");
        setConverter(new InternalConverter());
        // add event listeners
        valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                dateTimeValue.set(null);
            } else {
                if (dateTimeValue.get() == null) {
                    dateTimeValue.set(LocalDateTime.of(newValue, LocalTime.now()));
                } else {
                    LocalTime time = dateTimeValue.get().toLocalTime();
                    dateTimeValue.set(LocalDateTime.of(newValue, time));
                }
            }
        });
        dateTimeValue.addListener((obs, oldVal, newVal) -> setValue(newVal == null ? null : newVal.toLocalDate()));
        getEditor().focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                getEditor().fireEvent(
                        new KeyEvent(getEditor(), getEditor(), KeyEvent.KEY_PRESSED, null, null,
                                KeyCode.ENTER, false, false, false, false));
            }
        });
        dateTimeValue.setValue(localDateTime);
    }

    public DateTimePicker(Function<String> parsor) {
        this(LocalDateTime.now());
        this.parsor = parsor;
    }

    public LocalDateTime getDateTimeValue() {
        return dateTimeValue.get();
    }

    public void setDateTimeValue(LocalDateTime dateTimeValue) {
        if (dateTimeValue.isAfter(LocalDateTime.of(1971, 6, 30, 12, 0))) {
            this.dateTimeValue.set(dateTimeValue);
        } else {
            this.dateTimeValue.set(null);
        }
    }

    public ObjectProperty<LocalDateTime> dateTimeValueProperty() {
        return dateTimeValue;
    }

    public String getFormat() {
        return format.get();
    }

    public void setFormat(String format) {
        this.format.set(format);
    }

    public ObjectProperty<String> formatProperty() {
        return format;
    }

    class InternalConverter extends StringConverter<LocalDate> {
        public String toString(LocalDate object) {
            LocalDateTime value = getDateTimeValue();
            return (value != null) ? value.format(formatter) : "";
        }

        public LocalDate fromString(String value) {
            if (value == null || value.isEmpty()) {
                dateTimeValue.set(null);
                return null;
            }
            try {
                Parser.parseDateTime(value);
            } catch (Exception e) {
                dateTimeValue.set(dateTimeValue.get());
                return dateTimeValue.get().toLocalDate();
            }
            dateTimeValue.set(LocalDateTime.parse(value, formatter));
            return dateTimeValue.get().toLocalDate();
        }
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DateTimePickerSkin(this);
    }

    public String getParsedVal() {
        return parsedVal;
    }

    public String parse() throws ParseValueException {
        this.parsedVal = parsor
                .run(this.dateTimeValueProperty().getValue().format(DateTimeFormatter.ofPattern(DEFAULT_FORMAT)));
        return this.parsedVal;
    }
}

class DateTimePickerSkin extends DatePickerSkin {
    private final ObjectProperty<LocalTime> timeObjectProperty;
    private final Node popupContent;
    private final DateTimePicker dateTimePicker;
    private final Node timeSpinner;

    DateTimePickerSkin(DateTimePicker dateTimePicker) {
        super(dateTimePicker);
        this.dateTimePicker = dateTimePicker;
        timeObjectProperty = new SimpleObjectProperty<>(this, "displayedTime",
                LocalTime.from(dateTimePicker.getDateTimeValue()));

        CustomBinding.bindBidirectional(dateTimePicker.dateTimeValueProperty(), timeObjectProperty,
                LocalDateTime::toLocalTime,
                lt -> dateTimePicker.getDateTimeValue().withHour(lt.getHour()).withMinute(lt.getMinute()));

        popupContent = super.getPopupContent();
        popupContent.getStyleClass().add("date-time-picker-popup");

        timeSpinner = getTimeSpinner();
        ((VBox) popupContent).getChildren().add(timeSpinner);
    }

    private Node getTimeSpinner() {
        if (timeSpinner != null) {
            return timeSpinner;
        }
        // Creeate HH:mm Spinners
        final HourMinuteSpinner spinnerHours = new HourMinuteSpinner(0, 23,
                dateTimePicker.getDateTimeValue().getHour());
        CustomBinding.bindBidirectional(timeObjectProperty, spinnerHours.valueProperty(),
                LocalTime::getHour,
                hour -> timeObjectProperty.get().withHour(hour));
        final HourMinuteSpinner spinnerMinutes = new HourMinuteSpinner(0, 59,
                dateTimePicker.getDateTimeValue().getMinute());
        CustomBinding.bindBidirectional(timeObjectProperty, spinnerMinutes.valueProperty(),
                LocalTime::getMinute,
                minute -> timeObjectProperty.get().withMinute(minute));
        // Create Separator
        final Label labelTimeSeperator = new Label(":");

        // Create View
        HBox hBox = new HBox(5, new Label("Time:"), spinnerHours, labelTimeSeperator, spinnerMinutes);
        hBox.setPadding(new Insets(8));
        hBox.setAlignment(Pos.CENTER_LEFT);

        // Add event listeners
        registerChangeListener(dateTimePicker.valueProperty(), e -> {
            LocalDateTime dateTimeValue = dateTimePicker.getDateTimeValue();
            timeObjectProperty.set((dateTimeValue != null) ? LocalTime.from(dateTimeValue) : LocalTime.MIDNIGHT);
            dateTimePicker.fireEvent(new ActionEvent());
        });
        ObservableList<String> styleClass = hBox.getStyleClass();
        styleClass.add("month-year-pane");
        styleClass.add("time-selector-pane");
        styleClass.add("time-selector-spinner-pane");
        return hBox;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getPopupContent() {
        return popupContent;
    }

}

class CustomBinding {

    public static <A, B> void bindBidirectional(Property<A> propertyA, Property<B> propertyB, Function<A, B> updateB,
            Function<B, A> updateA) {
        addFlaggedChangeListener(propertyA, propertyB, updateB);
        addFlaggedChangeListener(propertyB, propertyA, updateA);
    }

    public static <A, B> void bind(Property<A> propertyA, Property<B> propertyB, Function<A, B> updateB) {
        addFlaggedChangeListener(propertyA, propertyB, updateB);
    }

    private static <X, Y> void addFlaggedChangeListener(ObservableValue<X> propertyX, WritableValue<Y> propertyY,
            Function<X, Y> updateY) {
        propertyX.addListener(new ChangeListener<>() {
            private boolean alreadyCalled = false;

            @Override
            public void changed(ObservableValue<? extends X> observable, X oldValue, X newValue) {
                if (alreadyCalled)
                    return;
                try {
                    alreadyCalled = true;
                    propertyY.setValue(updateY.apply(newValue));
                } finally {
                    alreadyCalled = false;
                }
            }
        });
    }
}
