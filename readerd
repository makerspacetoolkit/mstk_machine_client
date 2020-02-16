#!/usr/bin/env python3
# readerd: Reads RFID cards at machine terminal as a login method for users.
# Flask enpoint is provided for remote login, troubleshooting and simulating jobs 
# ProcBridge client talks to the Java UI

# - This uses the 24v air filter signal to determine jobtime.
# - We're adding 2 min of extra air filter time after the job is over. 
# - Use another relay in series with 24v air filter control to toggle
# air filter/assist.

import signal
import time
import MFRC522
import RPi.GPIO as GPIO
import sys
import os
import datetime
from flask import Flask, request, url_for, abort
import _thread
from procbridge import  procbridge
import spidev

spi = spidev.SpiDev()
spi.open(1, 2)
spi.max_speed_hz = 100000
spi.mode = 0b00

# Create an object of the class MFRC522
MIFAREReader = MFRC522.MFRC522()

air_filter_sense = 16
air_filter_power = 15

proc_host = '127.0.0.1'
client_port = 8877
client = procbridge.ProcBridge(proc_host, client_port)


# makes Flask stfu
import logging
log = logging.getLogger('werkzeug')
log.setLevel(logging.ERROR)

# quiets GPIO warnings
GPIO.setwarnings(False)

card_serial = ""
card_serialold = ""
count_error = 0

alarm_state = 0
previous_sense_state = 0

def setup():
      print('GPIO setup')
      GPIO.setmode(GPIO.BOARD)
      GPIO.setup(air_filter_sense, GPIO.IN, GPIO.PUD_UP)
      GPIO.setup(air_filter_power, GPIO.OUT)

def teardown():
      global run
      run = False
      rdr.cleanup()
      print('GPIO teardown')
      GPIO.cleanup()

def two_min_air_filter():
    GPIO.output(air_filter_power,GPIO.HIGH)
    time.sleep(120)
    GPIO.output(air_filter_power,GPIO.LOW)

def flip_filter_pin(state):
    GPIO.output(air_filter_sense,GPIO.state)
    

def turbine_state():
    global alarm_state
    x = 0 
    total_amps = 0
    while x <= 100:
        resp = spi.xfer2([0x00, 0x00])
        w = resp[0]
        w <<= 8
        w |= resp[1]
        refV = 3.3
        lsb = refV/4096
        mV= (w-2048)*lsb*1000
        amps = abs( mV/66 )
        #print('amps is: %s' % amps)
        total_amps = amps + total_amps
        #print('total_amps is: %s' % total_amps)
        #print(x)
        x = x + 1
     #   time.sleep(1)
    avg = ( total_amps / 100)
    #print('average is %s' %  avg)
    if avg > .1:
        #print("we're on.")
        return 1
    else:
        #print("we're off.")
        alarm_state = 1
        return 0

def read_rfid_gpio():
  global alarm_state
  global previous_sense_state
  while 1:
    (status,TagType,CardTypeRec) = MIFAREReader.MFRC522_Request(MIFAREReader.PICC_REQIDL)
    if status == MIFAREReader.MI_OK:
       cardTypeNo = CardTypeRec[1]*256+CardTypeRec[0]
       (status,uid,uidData) = MIFAREReader.MFRC522_Anticoll()
       if status == MIFAREReader.MI_OK:
         count_error = 0
         print(uidData)
         card_serial = MIFAREReader.list2HexStr(uidData)
         card_serial = str(int(card_serial,16))
         print(card_serial)
         # wait one sec. befor next read will be started
         time.sleep(1)
         if len(card_serial) == 10 or 9 or 8:
            print('{"uuid":"%s"}' % card_serial)
            print(client.request('echo', {"uuid":card_serial}))
         else: 
            print("not 10 or 8 length")
    #read filter sense pin (inverted because of pullup) and current          
    filter_pin_state = int(not GPIO.input(air_filter_sense)) 
   # print(filter_pin_state)
    if filter_pin_state and alarm_state is not 1: # and jobtime is over n?
       tstate = turbine_state() 
       print (' tstate is %s ' % tstate)
       if tstate == 0:
          print(client.request('echo', {"filter_alarm":"1"}))
    if previous_sense_state != filter_pin_state:
   #    print(client.request('echo', {"job":job_state}))
        print(client.request('echo', {"job":str(filter_pin_state)}))
    previous_sense_state =  filter_pin_state
    # add extra filter time
    if previous_sense_state != filter_pin_state and filter_pin_state == 0:
       _thread.start_new_thread(two_min_air_filter,())
    # wait a bit before next read will be started
    time.sleep(.1)

# Initialize the Flask application
app = Flask(__name__)
#app.config['DEBUG'] = True
app.config.update(
    JSONIFY_PRETTYPRINT_REGULAR=False
)

mylogfile = ('/var/log/%s.access.log' % os.path.basename(__file__))
log_level = 5 

def debug_message(current_log_level, message_level, message):
    timestamp = time.strftime('%Y%m%d_%H:%M:%S') 
    if message_level <= current_log_level:
       print('%s - %s' % (timestamp, message))
       logfile = open(mylogfile, "a");
       logfile.write("%s - %s" % (timestamp, message)) 
       logfile.write("\n") 
       logfile.close()  


@app.route('/client', methods = ['POST'])
def accept_card_uid():
    if request.method == 'POST':
        #print("this is a POST")
        try:
           card_serial = (request.form['uuid'])
           if len(card_serial) == 10 or 9 or 8:
               print(client.request('echo', {"uuid":card_serial}))
               return str('ok')
           else: 
               print("not 10 or 8 length")
        except:
           pass 
        try:    
           job_state = (request.form['job'])
           if job_state == "1" or "0":
               print(client.request('echo', {"job":job_state}))
               if job_state == "1":
                  print('state is 1') 
                  GPIO.setup(air_filter_sense, GPIO.IN, GPIO.PUD_DOWN)
               else:
                  print('state is 0') 
                  GPIO.setup(air_filter_sense, GPIO.IN, GPIO.PUD_UP)
               # for quick and dirty testing of interlockd, uncomment and change port
               #print(client.request('echo', {"pin":job_state}))
               return str('ok')
        except:
            pass
        try:
           in_service = (request.form['in_service'])
           if in_service == "1" or "0":
              print(client.request('echo', {"in_service":in_service}))
              return str('ok')
        except:
            pass
        try:
           filter_alarm = (request.form['filter_alarm'])
           if filter_alarm == "1" or "0":
              print(client.request('echo', {"filter_alarm":filter_alarm}))
              return str('ok')
        except:
            pass

_thread.start_new_thread(read_rfid_gpio,())
   
if __name__ == '__main__':
  setup()
  app.run(

        host="0.0.0.0",
        port=int("7000"),
  )
  try:
    for line in sys.stdin:
       if line.strip() == 'exit':
          break
  except KeyboardInterrupt:
     pass

  teardown()
  print('goodbye')


_thread.start_new_thread(read_rfid_gpio,())
   
if __name__ == '__main__':
  setup()
  app.run(

        host="0.0.0.0",
        port=int("7000"),
  )
  try:
    for line in sys.stdin:
       if line.strip() == 'exit':
          break
  except KeyboardInterrupt:
     pass

  teardown()
  print('goodbye')

