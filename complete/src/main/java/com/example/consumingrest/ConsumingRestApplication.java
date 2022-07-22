package com.example.consumingrest;

import java.util.ArrayList;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;

@SpringBootApplication
public class ConsumingRestApplication {

	private static final Logger log = LoggerFactory.getLogger(ConsumingRestApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ConsumingRestApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate123(RestTemplateBuilder builder) {
		log.info("restTemplate123() start...");
		return builder.build();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		log.info("run() start...");
		return args -> {
			// restT(restTemplate);
			webClientT2();
		};
	}

	public void restT(RestTemplate restTemplate) {
		log.info("restTemplate() start...");

		addrLst = new ArrayList<TbAddr>();

		for(int i = 0; i < 100; i++) {
			TbAddr[] addrArr = restTemplate.getForObject(
				// "http://bada.ai/addr.json",
				"http://bada.ai/siseapi/sise/addr?lwdgCd=11650107&srchDvcd=1&srchVal=빌",
				TbAddr[].class
			);
			for(TbAddr addr : addrArr) {
				// log.info("stnmAddr = " + addr.getStnmAddr());
				// log.info("sgguBldgNm = " + addr.getSgguBldgNm());
				addrLst.add(addr);
			}
			log.info("addrLst.size() = " + addrLst.size());
		}
	}

	public void webClientT() {
		WebClient client = WebClient.builder()
			.baseUrl("http://bada.ai")
			.build();
		Mono<TbAddr[]> mono = client.get().uri("/siseapi/sise/addr?lwdgCd=11650107&srchDvcd=1&srchVal=빌").accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.bodyToMono(TbAddr[].class);
		TbAddr[] addrArr = mono.share().block();
		for(TbAddr addr : addrArr) {
                        log.info("stnmAddr = " + addr.getStnmAddr());
                        log.info("sgguBldgNm = " + addr.getSgguBldgNm());
                }
	}

	ArrayList addrLst = null;

	public void webClientT2() {
		log.info("webClientT2() start...");

		addrLst = new ArrayList();

                WebClient client = WebClient.builder()
                        .baseUrl("http://bada.ai")
                        .build();
		for(int i = 0; i < 100; i++) {
                	client.get().uri("/siseapi/sise/addr?lwdgCd=11650107&srchDvcd=1&srchVal=빌")
                        	.retrieve()
				.bodyToMono(Iterable.class)
				.subscribe(addrArr -> {
					for(Object addr : addrArr) {
						addrLst.add(addr);
						log.info("addr = " + addr);
					}
					log.info("addrLst.size() = " + addrLst.size());
				});
		}

		log.info("webClientT2() end...");
        }
}
