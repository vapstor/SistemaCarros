package beans;

import entidades.dao.CrudDAO;
import exception.ErroSistema;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public abstract class CrudBeanLogin<E, D extends CrudDAO> {

    private String estadoTela = "buscar";//Inserir, Editar, Buscar
    
    private E entidade;
    private List<E> entidades;
    
    public void novo(){
        entidade =  criarNovaEntidade();
        mudarParaInseri();
    }
    
    public void buscar(){
        if(isBusca() == false){
           mudarParaBusca();
           return;
        }
        try {
            entidades = getDao().buscar();
            if(entidades == null || entidades.size() < 1){
                adicionarMensagem("Não temos nada cadastrado!", FacesMessage.SEVERITY_WARN);
            }
        } catch (ErroSistema ex) {
            Logger.getLogger(CrudBeanLogin.class.getName()).log(Level.SEVERE, null, ex);
            adicionarMensagem(ex.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    
    public void adicionarMensagem(String mensagem, FacesMessage.Severity tipoErro){
        FacesMessage fm = new FacesMessage(tipoErro, mensagem, null);
        FacesContext.getCurrentInstance().addMessage(null, fm);
    }
    
    //getters e setters
    public E getEntidade() {
        return entidade;
    }

    public void setEntidade(E entidade) {
        this.entidade = entidade;
    }

    public List<E> getEntidades() {
        return entidades;
    }

    public void setEntidades(List<E> entidades) {
        this.entidades = entidades;
    }
    
    //Responsvel por criar os métodos nas classes bean
    public abstract D getDao();
    public abstract E criarNovaEntidade();
    
    //Metodos para controle da tela
    public boolean isInseri(){
        return "inserir".equals(estadoTela);
    }
    public boolean isEdita(){
        return "editar".equals(estadoTela);
    }
    public boolean isBusca(){
        return "buscar".equals(estadoTela);
    }
    private boolean isAuth() {
//        throw new UnsupportedOperationException("teste");
        return "autenticar".equals(estadoTela);
    }
    
    
    
    public void mudarParaAuth(){
        estadoTela = "autenticar";
    }
    public void mudarParaInseri(){
        estadoTela = "inserir";
    }
    public void mudarParaEdita(){
        estadoTela = "editar";
    }
    public void mudarParaBusca(){
        estadoTela = "buscar";
    }
 
}
