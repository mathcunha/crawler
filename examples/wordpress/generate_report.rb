require 'json'
require 'net/http'
require 'logger'

class GenerateReport

  def initialize(server_ip:'54.82.195.19', server_port:9200)
     @server_ip = server_ip
     @server_port = server_port
     @workloads = [100,200,300,400,500,600,700,800,900,1000]
     @profile_ids = ['c3_large', 'c3_xlarge', 'c3_2xlarge']
     @logger = Logger.new('logfile.log')
  end

  def run
    puts "provider_id,#,workload,min,max,mean,count"
    for j in 1..4
      @profile_ids.each do |profile_id|
        @workloads.each do |workload|
           next if j == 3
           obj = get_statistics("#{j}_#{profile_id}",workload)
           puts "#{profile_id},#{j},#{workload},#{obj["facets"]["stat1"]["min"]},#{obj["facets"]["stat1"]["max"]},#{obj["facets"]["stat1"]["mean"]},#{obj["facets"]["stat1"]["count"]}"
        end
      end
    end
  end

  def get_statistics(scenario, workload)
    uri = URI("http://#{@server_ip}:#{@server_port}/_search?pretty=1")
    req = Net::HTTP::Post.new(uri)#, initheader = {'Content-Type' =>'text/plain'})
    #req.body = get_body_rt_gt_0(scenario, workload)
    req.body = get_body(scenario, workload)
    res = Net::HTTP.start(uri.hostname, uri.port) do |http|
      http.request(req)
    end
    JSON.parse(res.body)
  end
  def get_body(scenario, workload)
     body = ""
     	body += "{\n"
	body += "   \"query\" : {\n"
	body += "      \"bool\" : {\n"
	body += "         \"must\" : [            \n"
	body += "            {\n"
	body += "               \"term\" : {\n"
	body += "                  \"type\" : \"results\"\n"
	body += "               }\n"
	body += "            },\n"
	body += "	     {\n"
	body += "               \"term\" : {\n"
	body += "                  \"scenario\" : \"#{scenario}\"\n"
	body += "               }\n"
	body += "            },\n"
	body += "			{\n"
	body += "               \"term\" : {\n"
	body += "                  \"workload\" : \"#{workload}\"\n"
	body += "               }\n"
	body += "            }\n"
#        body += "	     ,{\n"
#	body += "		 \"query_string\" : {\n"
#        body += "                 \"fields\" : [\"status\"],\n"
#        body += "                 \"query\" : \"OK\"\n"
#        body += "                }\n"
#	body += "	     }\n"
	body += "         ]\n"
	body += "      }\n"
	body += "   },\n"
	body += "   \"from\" : 0,\n"
	body += "   \"size\" : 0,\n"
	body += "   \"facets\" : {\n"
	body += "        \"stat1\" : {\n"
	body += "            \"statistical\" : {\n"
	body += "                \"field\" : \"response_time\"\n"
	body += "            }\n"
	body += "        }\n"
	body += "    }\n"
	body += "}"
  end

  def get_body_rt_gt_0(scenario, workload)
     body = ""
        body += "{\n"
        body += "   \"query\" : {\n"
        body += "      \"bool\" : {\n"
        body += "         \"must\" : [            \n"
        body += "            {\n"
        body += "               \"term\" : {\n"
        body += "                  \"type\" : \"results\"\n"
        body += "               }\n"
        body += "            },\n"
        body += "            {\n"
        body += "               \"term\" : {\n"
        body += "                  \"scenario\" : \"#{scenario}\"\n"
        body += "               }\n"
        body += "            },\n"
        body += "                       {\n"
        body += "               \"term\" : {\n"
        body += "                  \"workload\" : \"#{workload}\"\n"
        body += "               }\n"
        body += "            },\n"
        body += "            {\n"
        body += "                \"range\" : {\n"
        body += "                  \"response_time\" : {\"gt\" : 0.0} \n"
        body += "                }\n"
        body += "            }\n"
        body += "         ]\n"
        body += "      }\n"
        body += "   },\n"
        body += "   \"from\" : 0,\n"
        body += "   \"size\" : 0,\n"
        body += "   \"facets\" : {\n"
        body += "        \"stat1\" : {\n"
        body += "            \"statistical\" : {\n"
        body += "                \"field\" : \"response_time\"\n"
        body += "            }\n"
        body += "        }\n"
        body += "    }\n"
        body += "}"
  end
end

GenerateReport.new.run
