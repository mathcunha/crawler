class ResultGroup
  attr_reader :requests, :ko, :ok, :response_time, :rt_ko, :rt_ok, :workload, :scenario
  
  def initialize(requests, workload, scenario)
    @requests = requests
    @results  = []
    @workload = workload
    @scenario = scenario
  end

  def add_request(result)
    @results << result
  end

  def summary
    @ko            = 0
    @ok            = 0
    @response_time = 0
    @rt_ko         = 0
    @rt_ok         = 0

    @results.each do |result|
      @response_time += result["_source"]["response_time"]
      if "KO".eql?(result["_source"]["status"])
        @rt_ko += result["_source"]["response_time"]
        @ko    += 1
      else
        @rt_ok += result["_source"]["response_time"]
        @ok    += 1
      end
    end

  end

  def to_s
    "\"#{@scenario}\",#{@workload},#{@response_time},#{@rt_ok},#{@rt_ko},#{length},#{@ok},#{@ko}"
  end
  
  def length
    @results.length
  end
end
