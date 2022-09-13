package com.example.consumingrest;

import java.net.URLEncoder;
import java.time.Duration;
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
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ConsumingRestApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(ConsumingRestApplication.class);

	public static void main(String[] args) {
		log.info("main() start...");
		for(String arg : args) {
			log.info("arg = " + arg);
		}
		SpringApplication.run(ConsumingRestApplication.class, args);
		log.info("main() end...");
	}

	public void run(String... args) throws Exception {
		log.info("run() start...");
		for(String arg : args) {
			log.info(">>> arg = " + arg);
		}
		// restT(restTemplate);
		// webClientT2();
		webFlux();
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

		String urlBuilder = new String("/siseapi/sise/addr");
		urlBuilder = urlBuilder.concat("?" + "lwdgCd" + "=" + "11650107");
		urlBuilder = urlBuilder.concat("&" + "srchDvcd" + "=" + "1");
		urlBuilder = urlBuilder.concat("&" + "srchVal" + "=" + "빌");

		log.info("urlBuilder = " + urlBuilder);

		for(int i = 0; i < 20; i++) {
                	client.get()
				.uri(urlBuilder)
				.accept(MediaType.APPLICATION_JSON)
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

		Flux.interval(Duration.ofSeconds(1))
			.subscribe(num -> {
				log.info("num = " + num);
			});

		log.info("webClientT2() end...");
        }

	public void webFlux() {

		Flux.interval(Duration.ofSeconds(600))
                        .subscribe(num -> {
                                log.info("ansin num = " + num);
				checkSise("https://ansim.hf.go.kr");
                        });

		Flux.interval(Duration.ofSeconds(600))
                        .subscribe(num -> {
                                log.info("bada num = " + num);
                                checkSise("http://bada.ai");
                        });

	}

	WebClient client = null;

	public void checkSise(String baseUrl) {

		if(client == null) {
			client = WebClient.builder()
                        	.baseUrl(baseUrl)
                        	.build();
		}

		String urlBuilder = new String("/siseapi/sise/addr3");
                urlBuilder = urlBuilder.concat("?" + "lwdgCd" + "=" + "26290106");
                urlBuilder = urlBuilder.concat("&" + "ltnoBno" + "=" + "1858");
                urlBuilder = urlBuilder.concat("&" + "ltnoBuno" + "=" + "");

                log.info("urlBuilder = " + urlBuilder);

		client.get()
			.uri(urlBuilder)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.bodyToMono(Iterable.class)
			.subscribe(addrArr -> {
				for(Object addr : addrArr) {
					log.info("addr = " + addr);
				}
		});
	}
}
