require 'json'
require 'redis'
require 'net/http'

class Execute

  attr_reader :redis
  def initialize(ip_redis:'127.0.0.1', port_redis:6379)
     @redis = Redis.new(:host => ip_redis, :port => port_redis)
     @workloads = [25,50,75,100,125,150,200,300,400,500,600,700,800,900,1000, 1250, 1500, 1750, 2000]
     #@profile_ids = ['1_c3_large', '2_c3_large', '1_c3_xlarge', '3_c3_large', '4_c3_large', '2_c3_xlarge', '1_c3_2xlarge', '3_c3_xlarge', '4_c3_xlarge', '2_c3_2xlarge', '3_c3_2xlarge', '4_c3_2xlarge']
     @profile_ids = ['1_c3_large',  '1_c3_xlarge', '1_c3_2xlarge']
  end

  def run
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

          #three executions
          for j in 0..2
            at_least_one_ko = false
            value = {"message"=>"init"}

            puts"#{profile_id} - #{workload} - #{count_ko}"

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
          puts"#{up_workload} - #{count_ko} - #{i}"
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
    puts command
    %x[ #{command} ]
  end

  def get_scenario(profile_id:, workload:200)
    profile = profile_id.split('_')
    method("get_scenario_#{profile[0]}").call(profile_id:"#{profile[1]}_#{profile[2]}", workload:workload)
  end

  def submit_scenario(scenario)
    request = Net::HTTP::Post.new('api/v1/benchmark/2', initheader = {'Content-Type' =>'text/plain'})
    request.body = scenario
    response = Net::HTTP.new('127.0.0.1', 28080).start {|http| http.request(request) }
    puts"#{response}"
  end

  def get_scenario_2(profile_id:, workload:200)
  end

  def get_scenario_3(profile_id:, workload:200)
  end

  def get_scenario_4(profile_id:, workload:200)
  end

  def get_scenario_1(profile_id:, workload:200)
    scenario += "!scenario"
	scenario += "    name: 1_c3_large"
	scenario += "    id: 1"
	scenario += "    endable: false"
	scenario += "    workload: !workload"
	scenario += "     targets:"
	scenario += "      - *gatling"
	scenario += "     functions:"
	scenario += "      - !workloadFunction"
	scenario += "       values: \"100\""
	scenario += "    metric:"
	scenario += "      nginx : *nginx"
	scenario += "      mysql : *mysql"
	scenario += "      wordpress : &wordpress !virtualMachine"
	scenario += "        id: 4"
	scenario += "        providerId: us-east-1/i-5f32c77e"
	scenario += "        type: *c3_large"
	scenario += "        name: wordpress        "
	scenario += "        scripts:"
	scenario += "          start_vm : "
	scenario += "            !scriptlet"
	scenario += "             scripts :"
	scenario += "              - \"~/change_database.sh ${scenarioScope.metric(mysql).publicIpAddress}\""
	scenario += "              - \"~/configHost.sh ${scenarioScope.metric(nginx).publicIpAddress}\""
	scenario += "              - 'sed -i \"s;\(host => \\"[^\\"]*\);host => \\"${scenarioScope.virtualMachines(gatling).publicIpAddress};g\" /home/ubuntu/logstash_metrics/indexer.conf'"
	scenario += "          start_metric : "
	scenario += "            !scriptlet"
	scenario += "              scripts:"
	scenario += "               - \"~/configMetric.sh ${scenarioScope.name}\""
	scenario += "          stop_metric : "
	scenario += "            !scriptlet"
	scenario += "              scripts:"
	scenario += "                - \"sudo service logstash-indexer stop\""
	scenario += "    virtualMachines:"
	scenario += "      gatling : *gatling"
	scenario += "      mysql : *mysql"
	scenario += "      nginx : *nginx"
    scenario
  end

#DELETE FROM `wp_posts`
#WHERE `post_type` = 'post'
#AND DATEDIFF(NOW(), `post_date`) > 600

end

execute = Execute.new
execute.run