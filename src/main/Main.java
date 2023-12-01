package main;

import java.util.Scanner;

import pessoa.Carteira;
import pessoa.Multas;
import pessoa.Pessoa;
import pessoa.Veiculo;
import admin.Admin;


public class Main {

    public static void main(String[] args) {

        Scanner entrada = new Scanner(System.in);

        System.out.println("Olá, seja bem-vindo ao SAGM, o Sistema Amador de Gerenciamento de Multas! Por favor, informe seu nome: ");

        String name = entrada.next();

        Pessoa per = new Pessoa();
        Carteira car = new Carteira(per);
        Veiculo vei = new Veiculo(per);       
        Multas multas = new Multas(car);
        Admin admin = new Admin(car, multas);

        if (!per.pesquisarNome(name) && !name.equals("Admin")) {
            System.out.println("Usuário não encontrado!");
            
        } else if (name.equals("Admin")) {
        	System.out.println("Seja Bem Vindo Administrador.");

            boolean sairAdmin = false;

            while (!sairAdmin) {
                System.out.println("Operações disponíveis para o Administrador:");
                System.out.println("1) Consultar tabelas associadas a um CPF. 2) Aplicar uma Multa. 3) Remover Multa. 4) Sair do programa");

                int optAdmin = 0;

                if (entrada.hasNextInt()) {
                    optAdmin = entrada.nextInt();
                } else {
                    System.out.println("Entrada inválida. Por favor, insira um número inteiro.");
                    entrada.next();
                    continue;
                }

                switch (optAdmin) {
                    case 1:
                        System.out.println("Informe o CPF para consultar as tabelas associadas: ");
                        String cpfAdmin = entrada.next();
                        admin.consultarTabelasPorCPF(cpfAdmin);
                        break;
                    case 4:
                        System.out.println("Saindo do modo administrador.");
                        sairAdmin = true;
                        break;
                    case 2:                    	
                        admin.inserirMulta();
                        break;
                    case 3:
                        System.out.println("Informe o CPF: ");
                        String cpfRemoverMulta = entrada.next();
                        admin.removerMulta(cpfRemoverMulta);
                        break;    
                    default:
                        System.out.println("Opção inválida. Por favor, escolha novamente.");
                }
            }
        }
        
        else {
            boolean sair = false;
            
            System.out.println("Seja Bem Vindo " + name);
            
            while (!sair) {
                System.out.println("Por favor selecione a operação. 1) Consultar meus dados. 2) Situação da Carteira. 3) Veículos cadastrados. 4)Consultar Detalhes da Multa. 5) Sair");

                int opt = 0;
                       
                if (entrada.hasNextInt()) {
                    opt = entrada.nextInt();
                } else {
                    System.out.println("Entrada inválida. Por favor, insira um número inteiro.");
                    entrada.next(); 
                    continue; 
                }

                switch (opt) {
                    case 1:
                        System.out.println(per.status(name));
                        break;
                    case 2:
                        System.out.println(car.statusCarteira(name));

                        if (car.getMulta() > 0) {
                            System.out.println("Multa encontrada! Deseja pagar a multa? (Digite 'sim' ou 'nao')");
                            String resposta = entrada.next();

                            if (resposta.equalsIgnoreCase("sim")) {
                                car.pagarMultas();
                                System.out.println("Multa zerada com sucesso! Dados atualizados: ");
                                System.out.println(car.statusCarteira(name));
                            } else {
                                System.out.println("Retornando a tela inicial...");
                            }
                        }
                        break;
                    case 3:
                        if (car.getCNH() != null) {        
                             System.out.println(vei.consultarVeiculos(car.getCNH()));
                         } else {
                             System.out.println("A CNH não está disponível. Consulte a situação da carteira primeiro.");
                         }
                        break;
                    case 4:
                        System.out.println("Digite a placa do veículo para consultar as multas: ");
                        String placa = entrada.next().toUpperCase();
                        System.out.println(multas.consultarMulta(per.getCPF(), placa));
                        break;    
                    case 5:
                        System.out.println("Finalizando o programa. Obrigado pela atenção!");
                        sair = true;
                        break;
                    default:
                        System.out.println("Opção inválida. Por favor, escolha novamente.");
                }
            }
        }
       entrada.close();
    }
}
