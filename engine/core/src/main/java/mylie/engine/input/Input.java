package mylie.engine.input;

@SuppressWarnings("unused")
public interface Input {
    record Key(String name) {
        public static final Key SPACE = new Key("SPACE");
        public static final Key ENTER = new Key("ENTER");
        public static final Key ESCAPE = new Key("ESCAPE");
        public static final Key UP = new Key("UP");
        public static final Key DOWN = new Key("DOWN");
        public static final Key LEFT = new Key("LEFT");
        public static final Key RIGHT = new Key("RIGHT");
        public static final Key W = new Key("W");
        public static final Key A = new Key("A");
        public static final Key B = new Key("B");
        public static final Key C = new Key("C");
        public static final Key D = new Key("D");
        public static final Key E = new Key("E");
        public static final Key F = new Key("F");
        public static final Key G = new Key("G");
        public static final Key H = new Key("H");
        public static final Key I = new Key("I");
        public static final Key J = new Key("J");
        public static final Key K = new Key("K");
        public static final Key L = new Key("L");
        public static final Key M = new Key("M");
        public static final Key N = new Key("N");
        public static final Key O = new Key("O");
        public static final Key P = new Key("P");
        public static final Key Q = new Key("Q");
        public static final Key R = new Key("R");
        public static final Key S = new Key("S");
        public static final Key T = new Key("T");
        public static final Key U = new Key("U");
        public static final Key V = new Key("V");
        public static final Key X = new Key("X");
        public static final Key Y = new Key("Y");
        public static final Key Z = new Key("Z");
        public static final Key NUM_0 = new Key("NUM_0");
        public static final Key NUM_1 = new Key("NUM_1");
        public static final Key NUM_2 = new Key("NUM_2");
        public static final Key NUM_3 = new Key("NUM_3");
        public static final Key NUM_4 = new Key("NUM_4");
        public static final Key NUM_5 = new Key("NUM_5");
        public static final Key NUM_6 = new Key("NUM_6");
        public static final Key NUM_7 = new Key("NUM_7");
        public static final Key NUM_8 = new Key("NUM_8");
        public static final Key NUM_9 = new Key("NUM_9");
        public static final Key NUMPAD_0 = new Key("NUMPAD_0");
        public static final Key NUMPAD_1 = new Key("NUMPAD_1");
        public static final Key NUMPAD_2 = new Key("NUMPAD_2");
        public static final Key NUMPAD_3 = new Key("NUMPAD_3");
        public static final Key NUMPAD_4 = new Key("NUMPAD_4");
        public static final Key NUMPAD_5 = new Key("NUMPAD_5");
        public static final Key NUMPAD_6 = new Key("NUMPAD_6");
        public static final Key NUMPAD_7 = new Key("NUMPAD_7");
        public static final Key NUMPAD_8 = new Key("NUMPAD_8");
        public static final Key NUMPAD_9 = new Key("NUMPAD_9");
        public static final Key NUMPAD_ENTER = new Key("NUMPAD_ENTER");
        public static final Key NUMPAD_PLUS = new Key("NUMPAD_PLUS");
        public static final Key NUMPAD_MINUS = new Key("NUMPAD_MINUS");
        public static final Key NUMPAD_TIMES = new Key("NUMPAD_TIMES");
        public static final Key NUMPAD_DIVIDE = new Key("NUMPAD_DIVIDE");
        public static final Key NUMPAD_DELETE = new Key("NUMPAD_DELETE");
        public static final Key SEMICOLON = new Key("SEMICOLON");
        public static final Key EQUAL = new Key("EQUAL");
        public static final Key LEFT_BRACKET = new Key("LEFT_BRACKET");
        public static final Key BACKSLASH = new Key("BACKSLASH");
        public static final Key RIGHT_BRACKET = new Key("RIGHT_BRACKET");
        public static final Key GRAVE_ACCENT = new Key("GRAVE_ACCENT");
        public static final Key WORLD_1 = new Key("WORLD_1");
        public static final Key WORLD_2 = new Key("WORLD_2");
        public static final Key TAB = new Key("TAB");
        public static final Key BACKSPACE = new Key("BACKSPACE");
        public static final Key INSERT = new Key("INSERT");
        public static final Key DELETE = new Key("DELETE");
        public static final Key PAGE_UP = new Key("PAGE_UP");
        public static final Key PAGE_DOWN = new Key("PAGE_DOWN");
        public static final Key HOME = new Key("HOME");
        public static final Key END = new Key("END");
        public static final Key CAPS_LOCK = new Key("CAPS_LOCK");
        public static final Key SCROLL_LOCK = new Key("SCROLL_LOCK");
        public static final Key NUM_LOCK = new Key("NUM_LOCK");
        public static final Key PRINT_SCREEN = new Key("PRINT_SCREEN");
        public static final Key PAUSE = new Key("PAUSE");
        public static final Key F1 = new Key("F1");
        public static final Key F2 = new Key("F2");
        public static final Key F3 = new Key("F3");
        public static final Key F4 = new Key("F4");
        public static final Key F5 = new Key("F5");
        public static final Key F6 = new Key("F6");
        public static final Key F7 = new Key("F7");
        public static final Key F8 = new Key("F8");
        public static final Key F9 = new Key("F9");
        public static final Key F10 = new Key("F10");
        public static final Key F11 = new Key("F11");
        public static final Key F12 = new Key("F12");
        public static final Key APOSTROPHE = new Key("APOSTROPHE");
        public static final Key COMMA = new Key("COMMA");
        public static final Key MINUS = new Key("MINUS");
        public static final Key PERIOD = new Key("PERIOD");
        public static final Key SLASH = new Key("SLASH");
        public static final Key LEFT_SHIFT = new Key("LEFT_SHIFT");
        public static final Key LEFT_CONTROL = new Key("LEFT_CONTROL");
        public static final Key LEFT_ALT = new Key("LEFT_ALT");
        public static final Key LEFT_SUPER = new Key("LEFT_SUPER");
        public static final Key RIGHT_SHIFT = new Key("RIGHT_SHIFT");
        public static final Key RIGHT_CONTROL = new Key("RIGHT_CONTROL");
        public static final Key RIGHT_ALT = new Key("RIGHT_ALT");
        public static final Key RIGHT_SUPER = new Key("RIGHT_SUPER");

        public static final Key UNKNOWN = new Key("UNKNOWN");
    }

    record MouseButton(String name) {
        public static final MouseButton LEFT = new MouseButton("LEFT");
        public static final MouseButton RIGHT = new MouseButton("RIGHT");
        public static final MouseButton MIDDLE = new MouseButton("MIDDLE");
        public static final MouseButton BUTTON_4 = new MouseButton("BUTTON_4");
        public static final MouseButton BUTTON_5 = new MouseButton("BUTTON_5");
        public static final MouseButton BUTTON_6 = new MouseButton("BUTTON_6");
        public static final MouseButton BUTTON_7 = new MouseButton("BUTTON_7");
        public static final MouseButton BUTTON_8 = new MouseButton("BUTTON_8");
        public static final MouseButton UNKNOWN = new MouseButton("UNKNOWN");
    }

    record GamepadButton(String name) {
        public static final GamepadButton A = new GamepadButton("CIRCLE");
        public static final GamepadButton B = new GamepadButton("CROSS");
        public static final GamepadButton X = new GamepadButton("SQUARE");
        public static final GamepadButton Y = new GamepadButton("TRIANGLE");
        public static final GamepadButton LEFT_BUMPER = new GamepadButton("LEFT_BUMPER");
        public static final GamepadButton RIGHT_BUMPER = new GamepadButton("RIGHT_BUMPER");
        public static final GamepadButton LEFT_THUMP = new GamepadButton("LEFT_THUMP");
        public static final GamepadButton RIGHT_THUMP = new GamepadButton("RIGHT_THUMP");
        public static final GamepadButton START = new GamepadButton("START");
        public static final GamepadButton SELECT = new GamepadButton("SELECT");
        public static final GamepadButton DPAD_LEFT = new GamepadButton("DPAD_LEFT");
        public static final GamepadButton DPAD_RIGHT = new GamepadButton("DPAD_RIGHT");
        public static final GamepadButton DPAD_UP = new GamepadButton("DPAD_UP");
        public static final GamepadButton DPAD_DOWN = new GamepadButton("DPAD_DOWN");
    }

    record GamepadAxis(String name) {
        public static final GamepadAxis LEFT_STICK_X = new GamepadAxis("LEFT_STICK_X");
        public static final GamepadAxis LEFT_STICK_Y = new GamepadAxis("LEFT_STICK_Y");
        public static final GamepadAxis RIGHT_STICK_X = new GamepadAxis("RIGHT_STICK_X");
        public static final GamepadAxis RIGHT_STICK_Y = new GamepadAxis("RIGHT_STICK_Y");
        public static final GamepadAxis LEFT_TRIGGER = new GamepadAxis("LEFT_TRIGGER");
        public static final GamepadAxis RIGHT_TRIGGER = new GamepadAxis("RIGHT_TRIGGER");
    }
}
