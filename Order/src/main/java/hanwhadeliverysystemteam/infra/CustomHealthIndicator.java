//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package hanwhadeliverysystemteam.infra;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/actuator"})
public class CustomHealthIndicator implements HealthIndicator {
    private final AtomicReference<Health> health = new AtomicReference(Health.up().build());

    public CustomHealthIndicator() {
    }

    public Health health() {
        return (Health)this.health.get();
    }

    @PutMapping({"/up"})
    public Health up() {
        Health up = Health.up().build();
        this.health.set(up);
        return up;
    }

    @PutMapping({"/down"})
    public Health down() {
        Health down = Health.down().build();
        this.health.set(down);
        return down;
    }

    @PutMapping({"/maintenance"})
    public Health maintenance() {
        Health maintenance = Health.status(new Status("MAINTENANCE", this.findMyHostname() + "/" + this.findMyIpAddress())).build();
        this.health.set(maintenance);
        return maintenance;
    }

    @GetMapping({"/echo"})
    public String echo() {
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        return this.findMyHostname() + "/" + this.findMyIpAddress();
    }

    private String findMyHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException var2) {
            return "unknown host name";
        }
    }

    private String findMyIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException var2) {
            return "unknown IP address";
        }
    }
}