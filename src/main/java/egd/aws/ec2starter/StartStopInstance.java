package egd.aws.ec2starter;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DryRunResult;
import com.amazonaws.services.ec2.model.DryRunSupportedRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * Starts or stops and EC2 instance
 */
@Slf4j
public class StartStopInstance {
    public static void startInstance(String instance_id, AmazonEC2 ec2) {
        DryRunSupportedRequest<StartInstancesRequest> dry_request = () -> {
            StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instance_id);

            return request.getDryRunRequest();
        };

        DryRunResult<StartInstancesRequest> dry_response = ec2.dryRun(dry_request);

        if (!dry_response.isSuccessful()) {
            System.out.printf("Failed dry run to start instance %s", instance_id);

            throw dry_response.getDryRunResponse();
        }

        StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instance_id);

        ec2.startInstances(request);

        System.out.printf("Successfully started instance %s", instance_id);
    }

    public static void stopInstance(String instance_id, AmazonEC2 ec2) {
        DryRunSupportedRequest<StopInstancesRequest> dry_request = () -> {
            StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instance_id);

            return request.getDryRunRequest();
        };

        DryRunResult<StopInstancesRequest> dry_response = ec2.dryRun(dry_request);

        if (!dry_response.isSuccessful()) {
            System.out.printf("Failed dry run to stop instance %s", instance_id);
            throw dry_response.getDryRunResponse();
        }

        StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instance_id);

        ec2.stopInstances(request);

        System.out.printf("Successfully stop instance %s", instance_id);
    }

    public static InstanceState checkEc2InstanceStatus(AmazonEC2 ec2, String ec2InstanceId, int maxStatusNumIntentos) {
        int intentos = 0;
        DescribeInstancesResult response = ec2.describeInstances();
        for (Reservation reservation : response.getReservations()) {
            for (Instance instance : reservation.getInstances()) {
                if (instance.getInstanceId().equals(ec2InstanceId)) {
                    return instance.getState();
                }
            }
        }
        if (intentos++ > maxStatusNumIntentos) {
            log.error("Se ha superado el numero de intentos para obtener el estatus de ls instancia " + ec2InstanceId);
            return null;
        }
        return null;
    }
}