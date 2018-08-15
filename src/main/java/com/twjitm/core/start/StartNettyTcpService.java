package com.twjitm.core.start;

import com.twjitm.core.initalizer.NettyTcpMessageServerInitializer;
import com.twjitm.core.spring.SpringServiceManager;
import com.twjitm.core.utils.logs.LoggerUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;

import java.net.InetAddress;


/**
 * @author �Ľ�
 * @date 2018/4/16
 */

public class StartNettyTcpService implements IStartService {
    private static Logger logger = LoggerUtils.getLogger(StartNettyTcpService.class);
    private int port = 9090;
    private String ip = "127.0.0.1";
    private static StartNettyTcpService startService;

    public static StartNettyTcpService getInstance() {
        if (startService == null) {
            startService = new StartNettyTcpService();
        }
        return startService;
    }

    public StartNettyTcpService() {
        // startService = new StartService();
    }

    @Override
    public void start() {
        EventLoopGroup listenIntoGroup = new NioEventLoopGroup();
        EventLoopGroup progressGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(listenIntoGroup, progressGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new NettyTcpMessageServerInitializer())
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture f;
        try {
            f = b.bind(ip, port).sync();
            logger.info("[---------------------TCP SERVICE START IS SUCCESSFUL IP=" + ip + "port number is :" + port + "------------]");
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("TCP START HAVE ERROR ,WILL STOP");
            SpringServiceManager.shutdown();
            e.printStackTrace();
            logger.error(e);
        } finally {
            listenIntoGroup.shutdownGracefully();
            progressGroup.shutdownGracefully();
            logger.info("SERVER WORLD STOP");
        }
    }

    @Override
    public void start(int port) throws Throwable {
        this.port = port;
        start();
    }

    @Override
    public void start(InetAddress inetAddress) throws Throwable {
        //  inetAddress.getHostName();
    }

    @Override
    public void stop() throws Throwable {

    }

}
