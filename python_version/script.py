import threading
import time
import timeit
import pyautogui
import mss
import mss.tools
from PIL import Image

from pynput.keyboard import Listener, KeyCode
from pynput.mouse import Button, Controller

delay = 0
button = Button.left
start_stop_key = KeyCode(char='s')
exit_key = KeyCode(char='e')


def get_status(list):
    for pixel in list:
        if pixel[0] == 126:
            return True
    return False


class ClickMouse(threading.Thread):
    def __init__(self, delay, button):
        super(ClickMouse, self).__init__()
        self.delay = delay
        self.button = button
        self.running = False
        self.program_running = True

    def start_chopping(self):
        self.running = True

    def stop_chopping(self):
        self.running = False

    def exit(self):
        self.stop_chopping()
        self.program_running = False

    def run(self):
        pos = 'l'
        while self.program_running:
            while self.running:
                with mss.mss() as sct:
                    monitor = {"top": 600, "left": 1310, "width": 50, "height": 30}
                    left = sct.grab(monitor)
                    monitor = {'top': 600, 'left': 1470, "width": 50, "height": 50}
                    right = sct.grab(monitor)

                    screen_left = Image.frombytes("RGB", left.size, left.bgra, "raw", "BGRX")
                    screen_right = Image.frombytes("RGB", right.size, right.bgra, "raw", "BGRX")

                left_obstructed = False
                right_obstructed = False

                if pos == 'l':
                    left_obstructed = get_status(list(screen_left.getdata()))
                if pos == 'r':
                    right_obstructed = get_status(list(screen_right.getdata()))

                if right_obstructed:
                    print("right side obstructed")
                    pyautogui.press('left')
                    pos = 'l'
                elif left_obstructed:
                    print("left side obstructed")
                    pyautogui.press('right')
                    pos = 'r'
                else:
                    print("no obstruction")
                    if pos == 'l':
                        pyautogui.press('left')
                    else:
                        pyautogui.press('right')



                # if not left_obstructed and not right_obstructed:
                #     print("no obstruction")
                #     if pos == 'l':
                #         pyautogui.moveTo(1357, 933)
                #         pyautogui.click()
                #     else:
                #         pyautogui.moveTo(1527, 938)
                #         pyautogui.click()
                # else:
                #     if right_obstructed and (pos == 'r'):
                #         print("right side obstructed")
                #         pyautogui.moveTo(1357, 933)
                #         pyautogui.click()
                #         pos = 'l'
                #     elif left_obstructed and (pos == 'l'):
                #         print("left side obstructed")
                #         pyautogui.moveTo(1527, 938)
                #         pyautogui.click()
                #         pos = 'r'
                #     else:
                #         print("obstruction on other side")
                #         if pos == 'l':
                #             pyautogui.moveTo(1357, 933)
                #             pyautogui.click()
                #         else:
                #             pyautogui.moveTo(1527, 938)
                #             pyautogui.click()
                # time.sleep(self.delay)
            time.sleep(0.000000000001)


mouse = Controller
click_thread = ClickMouse(delay, button)
click_thread.start()


def on_press(key):
    if key == start_stop_key:
        if click_thread.running:
            click_thread.stop_chopping()
        else:
            click_thread.start_chopping()
    elif key == exit_key:
        click_thread.exit()
        listener.stop()


with Listener(on_press=on_press) as listener:
    listener.join()

# button rightchop: x=1510, y=945
# button leftchop: x=1354, y=945
# start button:    x=1441, y=945
# right tree check: x = 1495, y=645
# left tree check: x = 1382, y=645


# color values:
"""
    blue = 211, 247, 255
    brown = 161, 116, 56
    
"""
