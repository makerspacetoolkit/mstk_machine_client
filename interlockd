#!/usr/bin/env python3
# interlockd: Provides a ProcBridge server for the Java UI to toggle an
# an enabling circut, such as a laser-cutter interlock. It responds with 
# a ProcBridge client call.

import RPi.GPIO as GPIO
import signal
import time
import sys
import os
import datetime
from procbridge import  procbridge
import _thread

proc_host = '127.0.0.1'
java_client_port = 8877
interlock_server_port = 7788

client = procbridge.ProcBridge(proc_host, java_client_port)

# quiets GPIO messages 
GPIO.setwarnings(False)

mylogfile = ('/var/log/%s.access.log' % os.path.basename(__file__))
log_level = 4 

machine_dict = {"4":"Laser","5":"CNC"}

#  using Normally Open relay
interlock = 13

alarm_pin = 32
alarm_state = 0

def setup():
      print('GPIO setup')
      GPIO.setmode(GPIO.BOARD)
      GPIO.setup(interlock, GPIO.OUT)
      GPIO.setup(alarm_pin, GPIO.OUT)
      GPIO.output(alarm_pin,GPIO.LOW)

def teardown():
      print('GPIO teardown')
      GPIO.output(interlock, GPIO.LOW)
      GPIO.cleanup()

def debug_message(current_log_level, message_level, message):
    timestamp = time.strftime('%Y%m%d_%H:%M:%S')
    if message_level <= current_log_level:
       #print('%s - %s' % (timestamp, message))
       logfile = open(mylogfile, "a");
       logfile.write("%s - %s" % (timestamp, message))
       logfile.write("\n")
       logfile.close()

def alarm_on():
    global alarm_state
    alarm_count = 0
    while 1:
       GPIO.output(alarm_pin,GPIO.HIGH)
       time.sleep(.5)
       GPIO.output(alarm_pin,GPIO.LOW)
       time.sleep(.5)
       alarm_count = alarm_count + 1
       print('alarm!')
       print(alarm_state)
       if alarm_count == 60:
           # disconnect interlock
           GPIO.output(interlock, GPIO.LOW)
       if alarm_state == 0:
           break

if __name__ == '__main__':

  setup()

  # define request handler
  def request_handler(api: str, arg: dict) -> dict:
      global alarm_state
      if api == 'echo':
          try:
              if 'pin' in arg:
                if arg['pin'] == 1:
                   print('arg pin is %s' % arg['pin'])
                   GPIO.output(interlock,GPIO.HIGH)
                   current_state = GPIO.input(interlock)
                   print('current state is now %s' % current_state)
                   debug_message(log_level,3, "Current pin state is now %s" % current_state)
                   print(client.request('echo', {'pin':current_state}))
                if arg['pin'] == 0:
                   print('arg pin is %s' % arg['pin'])
                   GPIO.output(interlock,GPIO.LOW)
                   current_state = GPIO.input(interlock)
                   print('current state is now %s' % current_state)
                   debug_message(log_level,3, "Current pin state is now %s" % current_state)
                   print(client.request('echo', {'pin':current_state}))
              elif 'filter_alarm' in arg:
                print(arg['filter_alarm'])
                if arg['filter_alarm'] == 1:
                   alarm_state = 1;
                   _thread.start_new_thread(alarm_on,())
                if arg['filter_alarm'] == 0:
                   alarm_state = 0;
              else:
                 print('no recognized paramter')
          except:              
             print('oh dear.')
          return arg
      else:
          print('unknown api')

  # start socket server
  server = procbridge.ProcBridgeServer(proc_host, interlock_server_port, request_handler)
  server.start()
  print('listening...')

  try:
      for line in sys.stdin:
          if line.strip() == 'exit':
              break
  except KeyboardInterrupt:
      pass

  teardown()
  server.stop()
  print('bye')


