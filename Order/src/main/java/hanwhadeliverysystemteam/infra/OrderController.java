package hanwhadeliverysystemteam.infra;

import hanwhadeliverysystemteam.domain.*;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
// @RequestMapping(value="/orders")
@Transactional
public class OrderController {

    @Autowired
    OrderRepository orderRepository;
    // keep

    @RequestMapping(
        value = "orders/{id}/cancel",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public Order cancel(
        @PathVariable(value = "id") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /order/cancel  called #####");
        Optional<Order> optionalOrder = orderRepository.findById(id);

        optionalOrder.orElseThrow(() -> new Exception("No Entity Found"));
        Order order = optionalOrder.get();
        order.cancel();
        
        orderRepository.delete(order);
        return order;
    }

    // liveness probe 테스트 
    @GetMapping("/callMemleak")
    public void callMemleak() {
        try{
            this.memLeak();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void memLeak() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        Class unsafeClass = Class.forName("sun.misc.Unsafe");
        Field f = unsafeClass.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        System.out.print("4..3..2..1...");
        try
        {
            for(;;)
            unsafe.allocateMemory(1024*1024);
        } catch(Error e) {
            System.out.println("Boom :)");
            e.printStackTrace();
        }
    }
}
