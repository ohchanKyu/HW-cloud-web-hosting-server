package kr.ac.dankook.CloudApiGateway.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kr.ac.dankook.CloudApiGateway.entity.Vm;
import kr.ac.dankook.VmActionProto;
import kr.ac.dankook.VmActionServiceGrpc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VmGrpcService {

    private final VmActionServiceGrpc.VmActionServiceBlockingStub stub;

    public VmGrpcService(
            @Value("${spring.grpc.server.port}") int port,
            @Value("${spring.grpc.server.host}") String grpcHost) {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(grpcHost, port)
                .usePlaintext()
                .build();
        stub = VmActionServiceGrpc.newBlockingStub(channel);
    }

    public VmActionProto.VmActionResponse sendVmActionInfo(Vm vm, VmActionProto.VmActionType type) {
        VmActionProto.VmActionRequest request = VmActionProto.VmActionRequest.newBuilder()
                .setVmId(vm.getId())
                .setName(vm.getMember().getName())
                .setAction(type)
                .build();
        return stub.handleVmAction(request);
    }
}
