package entidades.dao;

import entidades.Carro;
import conexao.FabricaConexao;
import conexao.SessionUtils;
import entidades.Usuario;
import exception.ErroSistema;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author vapstor
 */
public class CarroDAO implements CrudDAO<Carro> {

    DateFormat df = new SimpleDateFormat("yyy-MM-dd");
    UsuarioDAO userDAO;

    @Override
    public void salvar(Carro carro) throws ErroSistema {
        HttpSession session = SessionUtils.getSession();
        Usuario currentUser = (Usuario) session.getAttribute("usuario");
        try {
            Connection conexao = FabricaConexao.getConexao();
            PreparedStatement ps;
//            System.out.println("dono " +ldao.getId());
            if (carro.getId() == null) {
                ps = conexao.prepareStatement("INSERT INTO `Carros` (IdDono, `modelo`,`fabricante`,`cor`,`ano`, `NomeDono`) VALUES (?, ?, ?, ?, ?, ?)");
            } else {
                ps = conexao.prepareStatement("UPDATE Carros SET IdDono=?, modelo=?, fabricante=?, cor=?, ano=?, NomeDono=? where id=?");
                ps.setInt(6, carro.getId());
            }
            ps.setInt(1, currentUser.getId());
            ps.setString(2, carro.getModelo());
            ps.setString(3, carro.getFabricante());
            ps.setString(4, carro.getCor());
            ps.setDate(5, new Date(carro.getAno().getTime()));
            ps.setString(6, carro.getNomeDono());
            ps.execute();
            FabricaConexao.fecharConexao();
        } catch (NullPointerException | SQLException np) {
            throw new ErroSistema("Erro ao tentar salvar!", np);
        }
    }

    @Override
    public void deletar(Carro carro) throws ErroSistema {
        try {
            Connection conexao = FabricaConexao.getConexao();
            PreparedStatement ps = conexao.prepareStatement("DELETE FROM Carros WHERE id = ?");
            ps.setInt(1, carro.getId());
            ps.execute();
        } catch (SQLException ex) {
            adicionarMensagem("Erro ao deletar o carros!", FacesMessage.SEVERITY_INFO);
            throw new ErroSistema("Erro ao deletar o carro!", ex);
        }
    }

    @Override
    public List<Carro> buscar() throws ErroSistema {
        HttpSession session = SessionUtils.getSession();
        Usuario currentUser = (Usuario) session.getAttribute("usuario");

        //Aqui busca para a search
        if (currentUser.getId() == 0) {
            try {
                Connection conexao = FabricaConexao.getConexao();
                PreparedStatement ps = conexao.prepareStatement("SELECT * FROM Carros");
                ResultSet resultSet = ps.executeQuery();
                List<Carro> carros = new ArrayList<>();
                while (resultSet.next()) {
                    Carro carro = new Carro();
                    carro.setIdDono(resultSet.getInt("IdDono"));
                    carro.setId(resultSet.getInt("id"));
                    carro.setModelo(resultSet.getString("modelo"));
                    carro.setFabricante(resultSet.getString("fabricante"));
                    carro.setCor(resultSet.getString("cor"));
                    carro.setAno(resultSet.getDate("ano"));
                    carro.setApenasAno(formataSomenteAno(df.format(carro.getAno())));
                    carro.setNomeDono(resultSet.getString("NomeDono"));
                    carros.add(carro);
                }
                FabricaConexao.fecharConexao();
                return carros;

            } catch (SQLException ex) {
                adicionarMensagem("Erro ao buscar os carros!", FacesMessage.SEVERITY_INFO);
                throw new ErroSistema("Erro ao buscar os carros!", ex);
            }
        }

        //aqui busca para troca
        try {
            Connection conexao = FabricaConexao.getConexao();
            PreparedStatement ps = conexao.prepareStatement("SELECT * FROM Carros WHERE IdDono = ?");
            ps.setInt(1, currentUser.getId());
            ResultSet resultSet = ps.executeQuery();
            List<Carro> carros = new ArrayList<>();
            while (resultSet.next()) {
                Carro carro = new Carro();
                carro.setIdDono(resultSet.getInt("IdDono"));
                carro.setId(resultSet.getInt("id"));
                carro.setModelo(resultSet.getString("modelo"));
                carro.setFabricante(resultSet.getString("fabricante"));
                carro.setCor(resultSet.getString("cor"));
                carro.setAno(resultSet.getDate("ano"));
                carro.setApenasAno(formataSomenteAno(df.format(carro.getAno())));
                carro.setNomeDono(resultSet.getString("NomeDono"));
                carros.add(carro);
            }
            FabricaConexao.fecharConexao();
            return carros;

        } catch (SQLException ex) {
            adicionarMensagem("Erro ao buscar os carros!", FacesMessage.SEVERITY_INFO);
            throw new ErroSistema("Erro ao buscar os carros!", ex);
        }
    }

    @Override
    public List<Carro> trocar() throws ErroSistema {
        HttpSession session = SessionUtils.getSession();
        Usuario currentUser = (Usuario) session.getAttribute("usuario");
        try {
            Connection conexao = FabricaConexao.getConexao();
            PreparedStatement ps = conexao.prepareStatement("SELECT * FROM Carros WHERE IdDono <> ?");
            ps.setInt(1, currentUser.getId());
            ResultSet resultSet = ps.executeQuery();
            List<Carro> allCarros = new ArrayList<>();
            while (resultSet.next()) {
                Carro carro = new Carro();
                carro.setIdDono(resultSet.getInt("IdDono"));
                carro.setId(resultSet.getInt("id"));
                carro.setModelo(resultSet.getString("modelo"));
                carro.setFabricante(resultSet.getString("fabricante"));
                carro.setCor(resultSet.getString("cor"));
                carro.setAno(resultSet.getDate("ano"));
                carro.setApenasAno(formataSomenteAno(df.format(carro.getAno())));
                carro.setNomeDono(resultSet.getString("NomeDono"));
                allCarros.add(carro);
            }
            FabricaConexao.fecharConexao();
            return allCarros;
        } catch (SQLException ex) {
            adicionarMensagem("Erro ao buscar os carros!", FacesMessage.SEVERITY_INFO);
            throw new ErroSistema("Erro ao buscar os carros!", ex);
        }
    }

    public String formataSomenteAno(String data) {
        char[] dataChar = data.toCharArray();
        char[] dataFormatada = new char[4];
        String somenteAno;
        for (int i = 0; i < 4; i++) {
            dataFormatada[i] = dataChar[i];
        }
        somenteAno = String.valueOf(dataFormatada);
        return somenteAno;
    }

    public void adicionarMensagem(String mensagem, FacesMessage.Severity tipoErro) {
        FacesMessage fm = new FacesMessage(tipoErro, mensagem, null);
        FacesContext.getCurrentInstance().addMessage(null, fm);
    }

    public void efetuaTroca(Carro carro1, Carro carro2) throws ErroSistema {
        try {
            Connection conexao = FabricaConexao.getConexao();
            int idDonoCarro1 = carro1.getIdDono();

            PreparedStatement ps = conexao.prepareStatement("UPDATE Carros SET IdDono = ?, NomeDono=? WHERE id = ?");
            PreparedStatement ps2 = conexao.prepareStatement("UPDATE Carros SET IdDono = ?, NomeDono=? WHERE id = ?");

            ps.setInt(1, carro2.getIdDono());
            ps.setString(2, carro2.getNomeDono());
            ps.setInt(3, carro1.getId());

            ps2.setInt(1, idDonoCarro1);
            ps2.setString(2, carro1.getNomeDono());
            ps2.setInt(3, carro2.getId());

            ps.execute();
            ps2.execute();

            FabricaConexao.fecharConexao();
        } catch (SQLException ex) {
            throw new ErroSistema("Erro ao efetuar a troca!", ex);
        }
    }

}
