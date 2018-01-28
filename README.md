# mstk_machine_client

A collection of programs designed to run on an RFID enabled Raspberry Pi which is integrated into a (premuim)makerspace tool.

It provides:
- Access control based on various criteria
- Integration with the machinetimed server for billing.
- Maintenance Mode lockout
- Error handling

It consists of:
-a UI Written in Java 8
-a Python daemon for controlling relays
-another Python daemon for reading RFID cards and providing an web endpoint.


Java Library deps (Maven):

cd.connect.common:connect-java-jackson:1.12
co.gongzh.procbridge:procbridge:1.0.22

