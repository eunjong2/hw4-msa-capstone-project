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
// @RequestMapping(value="/menus")
@Transactional
public class MenuController {

    @Autowired
    MenuRepository menuRepository;
    @RequestMapping(
        value = "menus/{id}/accept",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public Menu accept(
        @PathVariable(value = "id") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /menu/accept  called #####");
        Optional<Menu> optionalMenu = menuRepository.findById(id);

        optionalMenu.orElseThrow(() -> new Exception("No Entity Found"));
        Menu menu = optionalMenu.get();
        menu.accept();
        menu.setCookStatus("Accepted");

        menuRepository.save(menu);
        return menu;
    }

    
    // keep

    @RequestMapping(
        value = "menus/{id}/finish",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public Menu finish(
        @PathVariable(value = "id") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /menu/finish  called #####");
        Optional<Menu> optionalMenu = menuRepository.findById(id);

        optionalMenu.orElseThrow(() -> new Exception("No Entity Found"));
        Menu menu = optionalMenu.get();
        menu.finish();
        menu.setCookStatus("Finished");
        menuRepository.save(menu);
        return menu;
    }
}
