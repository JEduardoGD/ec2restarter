package egd.aws.ec2starter.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.InstanceState;

import egd.aws.ec2starter.Ec2Statuses;
import egd.aws.ec2starter.StartStopInstance;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StartServiceImpl {
    Regions region = com.amazonaws.regions.Regions.US_EAST_2;
    AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withRegion(region).build();

    @Value("${aws.ec2.app.instance}")
    private String ec2AppInstanceId;

    @Value("${aws.ec2.db.instance}")
    private String ec2DbInstanceId;

    private int maxStatusNumIntentos = 100;

    @Scheduled(cron = "${cron.start.expression}")
    public void startApplication() {
        log.info("Iniciando proceso de arranque.");
        InstanceState dbInstanceState = StartStopInstance.checkEc2InstanceStatus(ec2, ec2DbInstanceId,
                maxStatusNumIntentos);
        if (dbInstanceState == null) {
            log.error("No se pudo obtener el estatus de la instancia DB");
            return;
        }

        if (dbInstanceState.getCode() == Ec2Statuses.STOPPED.getStatus()) {
            log.info("La instancia de base de datos se encuentra denenida, se inicia...");
            StartStopInstance.startInstance(ec2DbInstanceId, ec2);
        }

        int countCheckEc2DbInstance = 0;

        do {
            dbInstanceState = StartStopInstance.checkEc2InstanceStatus(ec2, ec2DbInstanceId, maxStatusNumIntentos);
            if (dbInstanceState == null) {
                log.error("No se pudo obtener el estatus de la instancia DB");
                return;
            }
            if (dbInstanceState.getCode() == Ec2Statuses.RUNNING.getStatus()) {
                log.info("La instancia de base de datos se esta ejecutando.");
                break;
            } else {
                log.warn("La instancia de base de datos se encuentra en estatus " + dbInstanceState.getCode());
            }

            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                log.error(e.getLocalizedMessage());
            }
            if (countCheckEc2DbInstance++ > 10) {
                log.error("Despues de diez intentos no pudo obtenerse el estatus de la instancia de base de datos.");
                break;
            }
        } while (dbInstanceState.getCode() != Ec2Statuses.RUNNING.getStatus());

        dbInstanceState = StartStopInstance.checkEc2InstanceStatus(ec2, ec2DbInstanceId, maxStatusNumIntentos);
        if (dbInstanceState.getCode() == Ec2Statuses.RUNNING.getStatus()) {
            InstanceState appInstanceState = StartStopInstance.checkEc2InstanceStatus(ec2, ec2AppInstanceId,
                    maxStatusNumIntentos);
            if (appInstanceState == null) {
                return;
            }

            if (appInstanceState.getCode() == Ec2Statuses.STOPPED.getStatus()) {
                log.info("La instancia de app esta detenida, se inicia...");
                StartStopInstance.startInstance(ec2AppInstanceId, ec2);
            }

            do {
                appInstanceState = StartStopInstance.checkEc2InstanceStatus(ec2, ec2DbInstanceId, maxStatusNumIntentos);
                if (appInstanceState == null) {
                    log.error("No se pudo obtener el estatus de la instancia App");
                    return;
                }
                if (appInstanceState.getCode() == Ec2Statuses.RUNNING.getStatus()) {
                    log.info("La instancia app se esta ejecutando.");
                    break;
                } else {
                    log.warn("La instancia app se encuentra en estatus " + dbInstanceState.getCode());
                }

                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    log.error(e.getLocalizedMessage());
                }
                if (countCheckEc2DbInstance++ > 10) {
                    log.error("Despues de diez intentos no pudo obtenerse el estatus la instancia app.");
                    break;
                }
            } while (dbInstanceState.getCode() != Ec2Statuses.RUNNING.getStatus());
        }
        log.info("Se termina proceso de arranque.");
    }
}
