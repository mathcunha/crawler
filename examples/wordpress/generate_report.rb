require 'json'
require 'net/http'
require 'logger'

class GenerateReport

  def initialize(server_ip:'127.0.0.1', server_port:9200)
     @server_ip = server_ip
     @server_port = server_port
     @workloads = [100,200,300,400,500,600,700,800,900,1000]
     @profile_ids = ['c3_large', 'c3_xlarge', 'c3_2xlarge']
     @requests = ['new post', 'view post', 'edit post', 'find posts']
     @logger = Logger.new('logfile.log')
  end

  def run
    puts "provider_id,instance,workload,request,min,max,mean,median,count"
    for j in 1..4
      @profile_ids.each do |profile_id|
        @workloads.each do |workload|
           @requests.each do |request|
             obj = get_statistics("#{j}_#{profile_id}",workload,request)
             median = get_median("#{j}_#{profile_id}", workload, request, obj["hits"]["total"])
             puts "#{profile_id},#{j},#{workload},#{request},#{obj["facets"]["stat1"]["min"]},#{obj["facets"]["stat1"]["max"]},#{obj["facets"]["stat1"]["mean"]},#{median},#{obj["facets"]["stat1"]["count"]}"
          end
        end
      end
    end
  end

  def get_statistics(scenario, workload, request)
    uri = URI("http://#{@server_ip}:#{@server_port}/_search?pretty=1")
    req = Net::HTTP::Post.new(uri)#, initheader = {'Content-Type' =>'text/plain'})
    #req.body = get_body_rt_gt_0(scenario, workload)
    req.body = get_body(scenario, workload, request)
    res = Net::HTTP.start(uri.hostname, uri.port) do |http|
      http.request(req)
    end
    JSON.parse(res.body)
  end

  def get_median(scenario, workload, request, hits)
    median_pos = hits / 2
    uri = URI("http://#{@server_ip}:#{@server_port}/_search?pretty=1")
    req = Net::HTTP::Post.new(uri)

    #puts "#{hits} - #{median_pos}"

    req.body = get_body(scenario, workload, request, from:median_pos)

    res = Net::HTTP.start(uri.hostname, uri.port) do |http|
      http.request(req)
    end

    obj = JSON.parse(res.body)

    if hits % 2 == 1
      obj["hits"]["hits"][1]["_source"]["response_time"]
    else
       (obj["hits"]["hits"][0]["_source"]["response_time"] + obj["hits"]["hits"][1]["_source"]["response_time"]) / 2.0
    end
    
  end

  def get_body(scenario, workload, request, from:0)
     body = ""
     	body += "{\n"

        body += "   \"sort\" : [\n"
        body += "      {\"response_time\" : {\"order\" : \"asc\"}}\n"
        body += "   ],\n"
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
	body += "            },\n"
        body += "			{\n"
        body += "			   \"match_phrase\" : {\n"
        body += "				  \"status\" : \"OK\"\n"
        body += "			   }\n"
        body += "			},\n"
        body += "			{\n"
        body += "			   \"match_phrase\" : {\n"
        body += "				  \"request\" : \"#{request}\"\n"
        body += "			   }\n"
        body += "			}\n"
	body += "         ]\n"
	body += "      }\n"
	body += "   },\n"
	body += "   \"from\" : #{from},\n"
	body += "   \"size\" : 2,\n"
	body += "   \"facets\" : {\n"
	body += "        \"stat1\" : {\n"
	body += "            \"statistical\" : {\n"
	body += "                \"field\" : \"response_time\"\n"
	body += "            }\n"
	body += "        }\n"
	body += "    }\n"
	body += "}"
        #puts "#{body}"

        body
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
