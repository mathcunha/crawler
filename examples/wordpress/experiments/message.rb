require 'redis'
Redis.new(:host => '127.0.0.1', :port => 6379).lpush('crawler', '{"message":"ACTIONt2 pages per usert1tStart of scenariot1341960144747t1341960144747t1341960144747t1341960144747tOKtStart of scenarior","@version":"1","@timestamp":"2012-07-10T22:42:24.747+00:00","type":"results","workload":10,"scenario":"m1.medium","host":"vagrant","path":"/home/vagrant/simulation.log","lixo":"2 pages per user","scenario_gatling":"1","request":"Start of scenario","req_ini":1341960144747,"req_fim":1341960144747,"res_ini":1341960144747,"res_fim":1341960144747,"status":"'+ARGV[0]+'","valid":"true","response_time":0,"latency":0}')
