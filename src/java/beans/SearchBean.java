package beans;

/**
 *
 * @author vapstor
 */
import conexao.SessionUtils;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import entidades.Carro;
import entidades.Usuario;
import entidades.dao.CarroDAO;
import entidades.dao.LoginDAO;
import exception.ErroSistema;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.servlet.http.HttpSession;

/**
 *
 * @author vapstor
 */
@ManagedBean
@SessionScoped
public class SearchBean extends CrudBean<Carro, CarroDAO> implements Serializable {

    private CarroDAO carroDAO;
    private List<Carro> data = null;
    private List<Carro> result = null;
    private String display = "";
    private Usuario usuarioLogado = new Usuario();
    private List<Carro> listaCarros;

    public SearchBean() throws ErroSistema {
        HttpSession session = SessionUtils.getSession();
        LoginDAO ld = new LoginDAO();
        this.usuarioLogado = ld.getUsuarioLogado("");
        session.setAttribute("usuario", usuarioLogado);
    }

    @Override
    public final void buscar() {
        if (usuarioLogado.getId() == 0) {
            try {
                this.entidades = getDao().buscar();
                if (entidades == null || entidades.size() < 1) {
                    adicionarMensagem("Não temos nada cadastrado!", FacesMessage.SEVERITY_WARN);
                }
            } catch (ErroSistema ex) {
                Logger.getLogger(CrudBean.class.getName()).log(Level.SEVERE, null, ex);
                adicionarMensagem(ex.getMessage(), FacesMessage.SEVERITY_ERROR);
            }
        }
    }

    @PostConstruct
    public void init() {
        buscar();
        data = this.entidades;
        result = new ArrayList<>();
    }

    public void search() {
        result.clear();
        for (Carro carro : data) {
            if (carro.getModelo().toLowerCase().startsWith(display.toLowerCase()) || 
                    carro.getNomeDono().toLowerCase().startsWith(display.toLowerCase())) {
                result.add(carro);
            }
        }

        
//        if (result.isEmpty()) {
//            result.add("Nada Consta!");
//        }
    }

    public List<Carro> getResult() {
        return result;
    }

    public void setResult(List<Carro> result) {
        this.result = result;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public List<Carro> getListaCarros() {
        return listaCarros;
    }

    public void setListaCarros(List<Carro> listaCarros) {
        this.listaCarros = listaCarros;
    }

    @Override
    public CarroDAO getDao() {
         if (carroDAO == null) {
            carroDAO = new CarroDAO();
        }
        return carroDAO;
    }

    @Override
    public Carro criarNovaEntidade() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
