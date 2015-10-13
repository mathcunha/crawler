require 'json'
require 'net/http'
require 'logger'
require 'date'

class GenerateReport

  def initialize(server_ip:'127.0.0.1', server_port:9200)
     @server_ip = server_ip
     @server_port = server_port
     @workloads = [100,200,300,400,500,600,700,800,900,1000]
     @profile_ids = ['m3_medium', 'm3_large', 'm3_xlarge', 'm3_2xlarge','c3_large', 'c3_xlarge', 'c3_2xlarge']
     #@profile_ids = ['m3_medium', 'm3_large', 'm3_xlarge', 'm3_2xlarge']
     @logger = Logger.new('logfile.log')
  end

  def run
    puts "provider_id,workload,vm_type,cpuidle,cpubusy,kbmemfree,kbmemused,kbswpfree,kbswpused"
    for j in 1..4
      @profile_ids.each do |profile_id|
        @workloads.each do |workload|
           range = get_range("#{j}_#{profile_id}", workload)
           #puts "#{range}"

           range["req_ini"] = DateTime.strptime(range["req_ini"], "%Q")
           range["req_fim"] = DateTime.strptime(range["req_fim"], "%Q")
           #puts "#{range}"

           obj = get_results(get_body_stat("#{j}_#{profile_id}", workload, range["req_ini"], range["req_fim"], 0, 0))
           total = obj["hits"]["total"].to_i
           i = 0
           step = 200
           while i < total do
             obj = get_results(get_body_stat("#{j}_#{profile_id}",workload, range["req_ini"], range["req_fim"], i, step))

             load_results(obj)

             i += step
          end
           
        end
      end
    end
  end

  def load_results(results)
    results["hits"]["hits"].each do |result|
      puts "\"#{result["_source"]["scenario"]}\",#{result["_source"]["workload"]},\"#{result["_source"]["vm_type"]}\",#{result["_source"]["cpuidle"]},#{result["_source"]["cpubusy"]},#{result["_source"]["kbmemfree"]},#{result["_source"]["kbmemused"]},#{result["_source"]["kbswpfree"]},#{result["_source"]["kbswpused"]}"
    end
  end

  def get_range(scenario, workload)
     range_ini = get_results(get_body(scenario, workload, 0, 1))
     total_events = range_ini["hits"]["total"].to_i
     range_fim = get_results(get_body(scenario, workload, total_events - 1, 1))
     result = "{\"workload\":#{workload} , \"scenario\" : \"#{scenario}\", \"req_ini\":\"#{range_ini["hits"]["hits"][0]["_source"]["req_ini"]}\", \"req_fim\":\"#{range_fim["hits"]["hits"][0]["_source"]["res_fim"]}\"}"
     JSON.parse(result)
  end

  def get_results(body)
    uri = URI("http://#{@server_ip}:#{@server_port}/_search?pretty=1")
    req = Net::HTTP::Post.new(uri)#, initheader = {'Content-Type' =>'text/plain'})
    #req.body = get_body_rt_gt_0(scenario, workload)
    req.body = body
    res = Net::HTTP.start(uri.hostname, uri.port) do |http|
      http.request(req)
    end
    JSON.parse(res.body)
  end

  def get_body(scenario, workload, from, size)
     body = ""
	body += "{\n"
	body += "   \"sort\" : [\n"
	body += "	  {\"req_ini\" : {\"order\" : \"asc\"}}\n"
	body += "   ],\n"
	body += "   \"query\" : {\n"
	body += "	  \"bool\" : {\n"
	body += "		 \"must\" : [            \n"
	body += "			{\n"
	body += "			   \"term\" : {\n"
	body += "				  \"type\" : \"results\"\n"
	body += "			   }\n"
	body += "			},\n"
	body += "			{\n"
	body += "			   \"term\" : {\n"
	body += "				  \"scenario\" : \"#{scenario}\"\n"
	body += "			   }\n"
	body += "			},\n"
	body += "					   {\n"
	body += "			   \"term\" : {\n"
	body += "				  \"workload\" : \"#{workload}\"\n"
	body += "			   }\n"
	body += "			}\n"
	body += "		 ]\n"
	body += "	  }\n"
	body += "   },\n"
	body += "   \"from\" : #{from},\n"
	body += "   \"size\" : #{size}\n"
	body += "}\n"    
     #puts "#{body}" 
     body
  end

  def get_body_stat(scenario, workload, ini, fim, from, size)
     body = ""
        body += "{\n"
        body += "   \"sort\" : [\n"
        body += "         {\"@timestamp\" : {\"order\" : \"asc\"}}\n"
        body += "   ],\n"
        body += "   \"query\" : {\n"
        body += "         \"bool\" : {\n"
        body += "                \"must\" : [            \n"
        body += "                       {\n"
        body += "                          \"term\" : {\n"
        body += "                                 \"type\" : \"sar_1_3\"\n"
        body += "                          }\n"
        body += "                       },\n"
        body += "                       {\n"
        body += "                          \"term\" : {\n"
        body += "                                 \"scenario\" : \"#{scenario}\"\n"
        body += "                          }\n"
        body += "                       },\n"
        body += "                       {\n"
        body += "                          \"term\" : {\n"
        body += "                                 \"workload\" : \"#{workload}\"\n"
        body += "                          }\n"
        body += "                       },\n"
        body += "                       {\n"
        body += "                          \"range\" : {\n"
        body += "                                  \"@timestamp\" : {\n"
        body += "                                          \"gte\" : \"#{ini}\",\n"
        body += "                                          \"lte\" : \"#{fim}\"\n"
        body += "                                  }\n"
        body += "                          }\n"
        body += "                       }\n"
        body += "                ]\n"
        body += "         }\n"
        body += "   },\n"
        body += "   \"from\" : #{from},\n"
        body += "   \"size\" : #{size}\n"
        body += "}\n"
     #puts "#{body}"
     body
  end

end

GenerateReport.new.run
