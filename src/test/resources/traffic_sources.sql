insert into traffic_source (id, name, url, username, password, active)
values (1,
        'Nighthawk AX5400',
        'http://10.0.0.1/traffic_meter.htm',
        'admin',
        'password',
        true),
       (2,
        'Nighthawk R7000',
        'http://172.16.0.1/traffic_meter.htm',
        'admin',
        'password123',
        false);

commit;