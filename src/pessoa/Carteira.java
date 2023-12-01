package pessoa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import conexao_SQL.Conexao_SQL;

public class Carteira {

    private Pessoa pessoa; 
    private Veiculo veiculo; 
    public String CNH;
    public int multa;

    public Carteira(Pessoa pessoa) {
        this.setPessoa(pessoa);
        this.veiculo = new Veiculo(pessoa); 
    }

    public String statusCarteira(String name) {
        try {
            Connection con = Conexao_SQL.run_connection();
            String query = "SELECT * FROM situacao_carteira WHERE nome LIKE ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, name);

            ResultSet rs = stmt.executeQuery();

            StringBuilder result = new StringBuilder();

            if (rs.next()) {
                this.CNH = rs.getString("CNH");
                this.multa = rs.getInt("valor_multa");

                result.append("\n//////////////////////////////////\n");
                result.append(String.format("CNH: %s \n", rs.getString("CNH")));
                result.append(String.format("Nome: %s \n", rs.getString("nome")));
                result.append(String.format("Número de veículos cadastrados: %s \n", rs.getString("qty_veiculos")));
                result.append(String.format("Valor total da multa: %s \n", rs.getString("valor_multa")));

                String infoVeiculos = veiculo.consultarVeiculosMultadosPorCNH(this.CNH);
                if (this.multa > 0)
                    result.append("\nVeículos Multados Associados:\n").append(infoVeiculos);

                return result.toString();
            } else {
                return "Nenhum resultado encontrado.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Erro ao obter dados.";
        }
    }

    public void pagarMultas() {
        try {
            Connection con = Conexao_SQL.run_connection();

            String queryMulta = "SELECT valor_multa FROM situacao_carteira WHERE CNH = ?";
            PreparedStatement stmtMulta = con.prepareStatement(queryMulta);
            stmtMulta.setString(1, CNH);
            ResultSet rsMulta = stmtMulta.executeQuery();

            double valorTotalMulta = 0;
            if (rsMulta.next()) {
                valorTotalMulta = rsMulta.getDouble("valor_multa");
            }

            String infoVeiculos = veiculo.consultarVeiculosMultadosPorCNH(this.CNH);

            if (infoVeiculos.isEmpty()) {
                System.out.println("Nenhum veículo multado associado encontrado.");
                return;
            }

            Scanner scanner = new Scanner(System.in);

            while (valorTotalMulta > 0) {

                System.out.println("Escolha qual veículo pagar (informe a placa):");
                String placaEscolhida = scanner.nextLine().toUpperCase();

                double valorMultaVeiculo = veiculo.obterValorMultaPorVeiculo(placaEscolhida);

                String updateQueryCarteira = "UPDATE situacao_carteira SET valor_multa = ? WHERE CNH = ?";
                PreparedStatement updateStmtCarteira = con.prepareStatement(updateQueryCarteira);
                updateStmtCarteira.setDouble(1, valorTotalMulta - valorMultaVeiculo);
                updateStmtCarteira.setString(2, CNH);
                updateStmtCarteira.executeUpdate();

                String updateQueryVeiculo = "UPDATE veiculo SET situacao_veiculo = 'REGULAR', valor_multa = 0 WHERE PLACA = ?";
                PreparedStatement updateStmtVeiculo = con.prepareStatement(updateQueryVeiculo);
                updateStmtVeiculo.setString(1, placaEscolhida);
                updateStmtVeiculo.executeUpdate();
                
                String deleteQueryMultas = "DELETE FROM multas WHERE cpf = (SELECT cpf FROM pessoa_fisica WHERE CNH = ?) AND placa_veiculo = ?";
                PreparedStatement deleteStmtMultas = con.prepareStatement(deleteQueryMultas);
                deleteStmtMultas.setString(1, CNH);
                deleteStmtMultas.setString(2, placaEscolhida);
                deleteStmtMultas.executeUpdate();

                System.out.println("Multa paga com sucesso!");

                queryMulta = "SELECT valor_multa FROM situacao_carteira WHERE CNH = ?";
                stmtMulta = con.prepareStatement(queryMulta);
                stmtMulta.setString(1, CNH);
                rsMulta = stmtMulta.executeQuery();

                if (rsMulta.next()) {
                    valorTotalMulta = rsMulta.getDouble("valor_multa");
                } else {
                    valorTotalMulta = 0;
                }

                if (valorTotalMulta > 0) {
                    System.out.println("Deseja pagar outra multa? (S/N)");
                    String escolha = scanner.nextLine().toUpperCase();
                    if (!escolha.equals("S")) {
                        System.out.println("Retornando ao menu principal.");
                        break;
                    }
                } else {
                    System.out.println("Todas as multas foram pagas. Retornando ao menu principal.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao pagar as multas.");
        }
    }
    
    public double getValorMulta(String cnh) {
        try {
            Connection con = Conexao_SQL.run_connection();
            String query = "SELECT valor_multa FROM situacao_carteira WHERE CNH = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, cnh);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("valor_multa");
            } else {
                return 0; 
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1; 
        }
    }
    
    public String getCNH() {
        return CNH;
    }

    public void setCNH(String cNH) {
        CNH = cNH;
    }
    
    public void setMulta(int multa) {
        this.multa = multa;
    }

    public int getMulta() {
        return multa;
    }

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
}
