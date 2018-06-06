package beans;

import conexao.SessionUtils;
import entidades.dao.CarroDAO;
import entidades.Carro;
import entidades.Usuario;
import exception.ErroSistema;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpSession;

@ManagedBean
@SessionScoped
public class CarroBean extends CrudBean<Carro, CarroDAO> implements Serializable {

    private CarroDAO carroDAO;
    private Usuario currentUser;
    private Carro carroSelecionado, carroParaTroca;
    private List<Carro> listaCarrosTroca;

    public CarroBean() {
        buscar();
    }

    public Carro getCarroSelecionado() {
        return carroSelecionado;
    }

    public void setCarroSelecionado(Carro carroSelecionado) {
        this.carroSelecionado = carroSelecionado;
    }

    public List<Carro> getListaCarrosTroca() {
        return listaCarrosTroca;
    }

    public void setListaCarrosTroca(List<Carro> listaCarrosTroca) {
        this.listaCarrosTroca = listaCarrosTroca;
    }

    @Override
    public CarroDAO getDao() {
        if (carroDAO == null) {
            carroDAO = new CarroDAO();
        }
        return carroDAO;
    }

    @Override
    public final void buscar() {
        try {
            this.entidades = getDao().buscar();
            if (entidades == null || entidades.size() < 1) {
                adicionarMensagem("Não temos nada cadastrado!", FacesMessage.SEVERITY_WARN);
            }
        } catch (ErroSistema ex) {
            Logger.getLogger(CrudBean.class.getName()).log(Level.SEVERE, null, ex);
            adicionarMensagem(ex.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
        this.setCarroParaTroca(null);

    }

    public void trocar(Carro carroPraTrocar) throws ErroSistema {
        mudarParaTroca();
        try {
            setListaCarrosTroca(getDao().trocar());
            this.entidadesTroca = getListaCarrosTroca();
            if (getListaCarrosTroca() == null || getListaCarrosTroca().size() < 1) {
                adicionarMensagem("Não temos carro para troca!", FacesMessage.SEVERITY_WARN);
            }
        } catch (ErroSistema ex) {
            Logger.getLogger(CrudBean.class.getName()).log(Level.SEVERE, null, ex);
//            adicionarMensagem(ex.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
        if (carroPraTrocar != null) {
            setCarroParaTroca(carroPraTrocar);
        }
    }

    public void efetuarTroca(Carro carroSelecionado) {
        if (!isTroca()) {
            mudarParaTroca();
        }
        int idDonoCarro;
        try {
            setCarroSelecionado(carroSelecionado);
            getDao().efetuaTroca(getCarroSelecionado(), carroParaTroca);
            idDonoCarro = carroParaTroca.getIdDono();
            carroParaTroca.setIdDono(carroSelecionado.getIdDono());
            carroSelecionado.setIdDono(idDonoCarro);
            adicionarMensagem("Troca efetuada com sucesso!", FacesMessage.SEVERITY_INFO);
            setCarroSelecionado(null);
            setCarroParaTroca(null);
            mudarParaBusca();
        } catch (ErroSistema ex) {
            Logger.getLogger(CrudBean.class.getName()).log(Level.SEVERE, null, ex);
//            adicionarMensagem(ex.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
        buscar();
    }

    @Override
    public Carro criarNovaEntidade() {
        HttpSession session = SessionUtils.getSession();
        currentUser = (Usuario) session.getAttribute("usuario");
        Carro c = new Carro();
        c.setIdDono(currentUser.getId());
        c.setNomeDono(currentUser.getNome());
        return c;
    }

    public Carro getCarroParaTroca() {
        return carroParaTroca;
    }

    public void setCarroParaTroca(Carro carroParaTroca) {
        this.carroParaTroca = carroParaTroca;
    }

}
