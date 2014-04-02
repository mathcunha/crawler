require 'json'
require 'redis'
require 'net/http'
require 'logger'

class Execute

  attr_reader :redis
  def initialize(ip_redis:'127.0.0.1', port_redis:6379, benchmark_id:3)
     @redis = Redis.new(:host => ip_redis, :port => port_redis)
     @workloads = [25,50,75,100,125,150,200,300,400,500,600,700,800,900,1000, 1250, 1500, 1750, 2000]
     #@profile_ids = ['1_c3_large', '2_c3_large', '1_c3_xlarge', '3_c3_large', '4_c3_large', '2_c3_xlarge', '1_c3_2xlarge', '3_c3_xlarge', '4_c3_xlarge', '2_c3_2xlarge', '3_c3_2xlarge', '4_c3_2xlarge']
     @profile_ids = ['1_c3_large',  '1_c3_xlarge', '1_c3_2xlarge']
     @benchmark_id = benchmark_id
     @logger = Logger.new('logfile.log')
  end

  def run
      Process.daemon(true)
      i = 0
      up_workload = true

      @profile_ids.each do |profile_id|
        count_ko = 0

        #policy
        if i == -1
          i = 0
          up_workload = true
        elsif i == @workloads.size
          i = @workloads.size - 1
        end
        #policy

        while(i >= 0 && i < @workloads.size)
          workload = @workloads[i]
          scenario = get_scenario(profile_id:profile_id, workload:workload)
          submit_scenario(scenario)

          #three executions
          for j in 0..2
            at_least_one_ko = false
            value = {"message"=>"init"}

            @logger.info "#{profile_id} - #{workload} - #{count_ko}"

            until verify_end(value)
              value = onmessage(@redis.blpop("crawler")[1])

              if verify_ko(value)
                at_least_one_ko=true
              end
            end

            #three executions - end
            if at_least_one_ko
              count_ko += 1
            else
              count_ko = 0
            end

            if(count_ko == 3)
              break
            end
          end

          #policy
          @logger.info "#{up_workload} - #{count_ko} - #{i}"
          if up_workload
            if count_ko == 3
              #start new profile
              up_workload = false
              break
            else
              i += 1
            end
          else
            if count_ko == 3
              i -= 1
            else
              i += 1
              up_workload = true
            end
          end
          #policy

        end
      end

  end

  def verify_ko(json_message)
    "KO".eql?(json_message["status"])
  end

  def verify_end(json_message)
    "END".eql?(json_message["status"])
  end

  def onmessage(message)
    JSON.parse(message)
  end


  def start_benchmark
    #submit benchmark.yml
    command = "curl -F filedata=@/home/ubuntu/wordpress.yml -i -X POST --header \"Content-Type: text/plain\" http://127.0.0.1:28080/api/v1/benchmark"
    @logger.info  command
    %x[ #{command} ]
  end

  def get_scenario(profile_id:, workload:200)
    profile = profile_id.split('_')
    method("get_scenario_#{profile[0]}").call(profile_id:"#{profile[1]}_#{profile[2]}", workload:workload)
  end

  def submit_scenario(scenario)
    @logger.info "new scenario - #{scenario}"
    #request = Net::HTTP::Post.new("api/v1/benchmark/#{@benchmark_id}", initheader = {'Content-Type' =>'text/plain'})
    #response = Net::HTTP.new('127.0.0.1', 28080).start {|http| http.request(request) }
    uri = URI("http://127.0.0.1:28080/api/v1/scenario/#{@benchmark_id}")
    req = Net::HTTP::Post.new(uri, initheader = {'Content-Type' =>'text/plain'})
    req.body = scenario
    res = Net::HTTP.start(uri.hostname, uri.port) do |http|
      http.request(req)
    end
    @logger.info "resposta - #{res.value}"
  end

  def get_scenario_2(profile_id:, workload:200)
  end

  def get_scenario_3(profile_id:, workload:200)
  end

  def get_scenario_4(profile_id:, workload:200)
  end
  
  def get_scenario_1(profile_id:, workload:200)
    scenario = "!scenario\n"
	scenario += "    name: 1_#{profile_id}\n"
	scenario += "    id: 1\n"
	scenario += "    endable: false\n"
	scenario += "    workload: !workload\n"
	scenario += "     targets:\n"
	scenario += "      - *gatling\n"
	scenario += "     functions:\n"
	scenario += "      - !workloadFunction\n"
	scenario += "       values: \"#{workload}\"\n"
	scenario += "    metric:\n"
	scenario += "      nginx : *nginx\n"
	scenario += "      mysql : *mysql\n"
	scenario += "      wordpress : &wordpress !virtualMachine\n"
	scenario += "        id: 4\n"
	scenario += "        providerId: us-east-1/i-5f32c77e\n"
	scenario += "        type: *#{profile_id}\n"
	scenario += "        name: wordpress   \n"     
	scenario += "        scripts:\n"
	scenario += "          start_vm : \n"
	scenario += "            !scriptlet\n"
	scenario += "             scripts :\n"
	scenario += "              - \"~/change_database.sh ${scenarioScope.metric(mysql).publicIpAddress}\"\n"
	scenario += "              - \"~/configHost.sh ${scenarioScope.metric(nginx).publicIpAddress}\"\n"
	scenario += '              - \'sed -i "s;\(host => \"[^\"]*\);host => \"${scenarioScope.virtualMachines(gatling).publicIpAddress};g" /home/ubuntu/logstash_metrics/indexer.conf\'' + "\n"
	scenario += "          start_metric : \n"
	scenario += "            !scriptlet\n"
	scenario += "              scripts:\n"
	scenario += "               - \"~/configMetric.sh ${scenarioScope.name}\"\n"
	scenario += "          stop_metric : \n"
	scenario += "            !scriptlet\n"
	scenario += "              scripts:\n"
	scenario += "                - \"sudo service logstash-indexer stop\"\n"
	scenario += "    virtualMachines:\n"
	scenario += "      gatling : *gatling\n"
	scenario += "      mysql : *mysql\n"
	scenario += "      nginx : *nginx\n"
    scenario
  end
  
#DELETE FROM `wp_posts`
#WHERE `post_type` = 'post'
#AND DATEDIFF(NOW(), `post_date`) > 600

end

execute = Execute.new
execute.run
