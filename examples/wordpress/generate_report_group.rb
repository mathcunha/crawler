require 'json'
require 'net/http'
require 'logger'
require_relative 'result_group.rb'

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
    puts "provider_id,workload,response_time"
    requests = ['new post', 'view post', 'edit post', 'find posts', 'edit post']
    for j in 1..4
      @profile_ids.each do |profile_id|
        @workloads.each do |workload|
           obj = get_results("#{j}_#{profile_id}",workload, 0, 0)
           total = obj["hits"]["total"].to_i
           i = 0
           step = 200
           result_group = ResultGroup.new(requests, workload, "#{j}_#{profile_id}")
           response_time = 0
           while i < total do
             obj = get_results("#{j}_#{profile_id}",workload, i, step)
             
             #load_results(obj)
             
             obj["hits"]["hits"].each do |result|
               if requests[result_group.length].eql?(result["_source"]["request"])
                 result_group.add_request(result)
               else
                 @logger.info ("\"#{result["_source"]["scenario"]}\", #{requests[result_group.length]}, #{result["_source"]["request"]}, #{i}, #{result["_source"]["workload"]}")
                 result_group = ResultGroup.new(requests, workload, "#{j}_#{profile_id}")
               end

               if result_group.length == requests.length 
                 result_group.summary
                 puts "#{result_group}"
                 result_group = ResultGroup.new(requests, workload, "#{j}_#{profile_id}")
               end
             end
             
             i += step
          end
        end
      end
    end
  end

  def load_results(results)
    results["hits"]["hits"].each do |result|
      puts "\"#{result["_source"]["scenario"]}\",#{result["_source"]["workload"]},\"#{result["_source"]["request"]}\",#{result["_source"]["response_time"]}"
    end
  end

  def get_results(scenario, workload, from, size)
    uri = URI("http://#{@server_ip}:#{@server_port}/_search?pretty=1")
    req = Net::HTTP::Post.new(uri)#, initheader = {'Content-Type' =>'text/plain'})
    #req.body = get_body_rt_gt_0(scenario, workload)
    req.body = get_body(scenario, workload, from, size)
    res = Net::HTTP.start(uri.hostname, uri.port) do |http|
      http.request(req)
    end
    JSON.parse(res.body)
  end

  def get_body(scenario, workload, from, size)
     body = ""
	body += "{\n"
	body += "   \"sort\" : [\n"
	body += "	  {\"scenario_gatling\" : {\"order\" : \"asc\"}},\n"
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
	#body += "		   ,{\n"
	#body += "			  \"match_phrase\" : {\n"
	#body += "					 \"status\" : \"OK\"\n"
	#body += "			  }\n"
	#body += "		   }\n"
	body += "		 ]\n"
	body += "	  }\n"
	body += "   },\n"
	body += "   \"from\" : #{from},\n"
	body += "   \"size\" : #{size}\n"
	body += "}\n"    
     #puts "#{body}" 
     body
  end
end

GenerateReport.new.run
