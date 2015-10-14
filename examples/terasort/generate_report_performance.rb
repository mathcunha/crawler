require 'csv'
require 'json'
require 'net/http'
require 'logger'
require 'date'

class GenerateReport
  def initialize(server_ip:'23.22.243.1', server_port:9200)
	  @server_ip=server_ip
	  @server_port=server_port
     @result = Hash.new
	 path = File.expand_path('../', __FILE__)
	 file = File.join( path, "results.csv" )
	 
	 CSV.foreach(file, headers: true) do |row|
	@result["#{row["scenario"]}+#{row["workload"]}"] =
		{ 	timestamp: row["timestamp"],
			ini: row["ini"],
			fim: row["fim"]}
	end
	 
  end
  def run
	puts "scenario,workload,memused,cpuused"
	@result.each  do |key, value| 		
		ini = DateTime.strptime(value[:ini], "%Q")
		fim = DateTime.strptime(value[:fim], "%Q")
		
		aKey = key.split("+")
		scenario = aKey[0]
		workload = aKey[1]
		
		get_body_stat(scenario, workload, ini, fim, 0, 0)
		obj = get_results(get_body_stat(scenario, workload, ini, fim, 0, 0))
        total = obj["hits"]["total"].to_i
        i = 0
        step = 200
        while i < total do
			obj = get_results(get_body_stat(scenario, workload, ini, fim, i, step))
			load_results(obj)
            i += step
        end
	end
  end
  def load_results(results)
    results["hits"]["hits"].each do |result|
		puts "\"#{result["_source"]["Scenario"]}\",#{result["_source"]["Workload"]},#{result["_source"]["MemFree"]*1.0/result["_source"]["MemTotal"]},#{100.0-result["_source"]["CPU"]["Idle"]}"
	end
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
  def get_body_stat(scenario, workload, ini, fim, from, size)
     body = ""
        body += "{\n"
        body += "   \"query\" : {\n"
        body += "         \"bool\" : {\n"
        body += "                \"must\" : [            \n"
        body += "                       {\n"
        body += "                          \"term\" : {\n"
        body += "                                 \"Scenario\" : \"#{scenario}\"\n"
        body += "                          }\n"
        body += "                       },\n"
        body += "                       {\n"
        body += "                          \"term\" : {\n"
        body += "                                 \"Workload\" : \"#{workload}\"\n"
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
		body += "   \"filter\" :{\n"
        body += "		\"exists\" : { \"field\" : \"MemFree\" }\n"
        body += "	},\n"
        body += "   \"from\" : #{from},\n"
        body += "   \"size\" : #{size}\n"
        body += "}\n"
     #puts "#{body}"
     body
  end
end

GenerateReport.new.run
